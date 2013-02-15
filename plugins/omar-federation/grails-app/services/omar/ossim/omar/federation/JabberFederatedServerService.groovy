package omar.ossim.omar.federation

import grails.converters.JSON
import org.apache.xerces.util.ParserConfigurationSettings
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.packet.VCard
import org.jivesoftware.smackx.provider.VCardProvider
import org.ossim.omar.federation.FederatedServer
import org.ossim.omar.federation.JabberParticipantListener
import org.springframework.beans.factory.InitializingBean
import org.ossim.omar.core.ConfigSettings
import groovy.json.JsonBuilder

class JabberFederatedServerService implements InitializingBean{
    def grailsApplication
    def federationConfigSettingsService
    def vCard
    def jabberDomain
    def jabberPort
    def jabberUser
    def jabberPassword
    def jabberChatRoomId
    def jabberChatRoomPassword
    def jabber
    def participantListener
    def enabled
    void loadFromTable(){
        def record = federationConfigSettingsService.getSettingsRecord()
        if(record)
        {
            def settings = JSON.parse(record.settings);
            if(settings)
            {
                vCard = new VCard()
                vCard.setNickName(settings?.vcard?.nickName);
                vCard.setFirstName(settings?.vcard?.firstName);
                vCard.setLastName(settings?.vcard?.lastName);

                vCard.setField("IP", settings?.vcard?.IP)
                vCard.setField("URL", settings?.vcard?.URL)

                jabberDomain              = settings?.server?.ip
                jabberPort                = Integer.parseInt(settings?.server?.port)
                jabberUser                = settings?.server?.username
                jabberPassword            = settings?.server?.password
                jabberChatRoomId          = settings?.chatRoom?.id
                jabberChatRoomPassword    = settings?.chatRoom?.password
                enabled = settings?.chatRoom?.enabled
                vCard.setJabberId("${settings?.vcard?.IP}@${jabberDomain}")//"${config?.omar?.serverIP}@${jabberDomain}")
            }
           //println vCard.toString()
           //println "${jabberDomain}, ${jabberPort}, ${jabberUser}, ${jabberPassword}, ${jabberChatRoomId}, ${jabberChatRoomPassword}"
        }
    }
    def isConnected()
    {
        if (!jabber||!jabber?.connection||!jabber.chatRoom) return false;
        jabber?.connection?.isConnected()?true:false;
    }
    def refreshServerTable()
    {
        def vcardList = getAllVCards()
        FederatedServer.withTransaction {
            FederatedServer.executeUpdate('delete from FederatedServer')

            def fullUserId = makeFullUserNameAndId(vCard.getField("IP"));
                def federatedServer = new FederatedServer([serverId:fullUserId.id,
                        available: true,
                        vcard: vCard.toString()])
            federatedServer.save()

            vcardList.each{vcard->
                def ip = vcard.getField("IP")
                if (ip)
                {
                    makeAvailable(vcard.getField("IP"))
                }
            }
        }
    }
    def makeFullUserNameAndId(def userName)
    {
        def full = userName + "@" + jabberDomain
        def fullId = full.replaceAll(~/\@|\.|\ |\&/, "")
        return [user:full, id:fullId]
    }
    def makeAvailable(def userName)
    {
        def fullUserId = makeFullUserNameAndId(userName)//userName + "@" + jabberDomain
        def tempCard = new VCard();
        try{
            tempCard.load(jabber.connection, fullUserId.user);
            def ip =  tempCard.getField("IP");
            if(ip)
            {
                FederatedServer.withTransaction{
                    def federatedServer = new FederatedServer([serverId:fullUserId.id,
                            available: true,
                            vcard: tempCard.toString()])
                    federatedServer.save()
                }
            }
        }
        catch(def e)
        {
        }
    }
    def makeUnavailable(def userName)
    {
        def fullUser = makeFullUserNameAndId(userName)
        FederatedServer.withTransaction{
            FederatedServer.where{serverId==fullUser.id}.deleteAll()
        }
    }
    def getServerList()
    {
        def result = []
        FederatedServer.withTransaction{
            FederatedServer.findAll(sort:"id", order: 'asc').each{server->
                def vcard = VCardProvider.createVCardFromXML(server.vcard)
                result << [
                        id: server.serverId,
                        firstName:vcard.firstName,
                        lastName:vcard.lastName,
                        nickname:vcard.nickName,
                        organization:vcard.organization,
                        ip:vcard.getField("IP"),
                        url:vcard.getField("URL"),
                        phone:vcard.getPhoneHome("VOICE")?:vcard.getPhoneWork("VOICE")
                ]
            }
        }

        result
    }
    def reconnect()
    {
        if (isConnected())
        {
            disconnect()
        }
        loadFromTable()

        connect()
    }
    def disconnect()
    {
        try{

            if(isConnected())
            {
                jabber.chatRoom.removeParticipantStatusListener(participantListener)
                jabber?.connection.disconnect()
            }
        }
        catch(def e)
        {

        }
        jabber = [:]
    }
    def connect()
    {
        if(!enabled)
        {
            if (isConnected())
            {
                disconnect()
            }
            refreshServerTable()
            return jabber
        }
        if (isConnected())
        {
            refreshServerTable()
            return jabber
        }
        jabber = [:]
        try{
            jabber.config     =  new ConnectionConfiguration(jabberDomain,
                                                             jabberPort);
            jabber.connection = new XMPPConnection(jabber.config);
            jabber.connection.connect();
            jabber.connection.login(jabberUser, jabberPassword)
        }
        catch(def e)
        {
            refreshServerTable()
            //log.error(e2)
            return [:]
        }
        if (jabber.connection.isAuthenticated())
        {
            try{

                vCard.save(jabber.connection)
                jabber.chatRoom = new MultiUserChat(jabber.connection,
                        "${jabberChatRoomId}")

                jabber.chatRoom.join(jabberUser,
                        "${jabberChatRoomPassword}")

                participantListener = new JabberParticipantListener([federatedServerService:this])
                jabber.chatRoom.addParticipantStatusListener(participantListener)
            }
            catch(def e)
            {
                //println e
                jabber.chatRoom = null
                disconnect();
                jabber.connection = null
                //println e
               // refreshServerTable()
               // return [:]
            }
        }
        refreshServerTable()
        return jabber
    }
    /*
    def createAdminConnection()
    {
        def result = [connection:null,
                config: null,
                chatRoom: null]
        def config     =  new ConnectionConfiguration(jabberDomain,
                jabberPort);
        def connection = new XMPPConnection(config);
        try{
            connection.connect();
            connection.login(jabberAdminUser, jabberAdminPassword);
        }
        catch(e)
        {
            connection = null

        }
        if (connection)
        {

        }
        def chatRoom = null
        if (connection)
        {
            chatRoom = new MultiUserChat(connection,
                    "${jabberChatRoomId}")
            try{
                chatRoom.join(jabberAdminUser,
                        "${jabberChatRoomPassword}")
            }
            catch(e)
            {
                chatRoom = null
                connection.disconnect();
                connection = null
            }
        }
        result = [
                connection:connection,
                config:config,
                chatRoom:chatRoom
        ]
        result
    }
    */
    def getAllVCards()
    {
        def vCards = []

        if (isConnected()&&jabber.chatRoom)
        {
            def occupants = jabber.chatRoom?.occupants
            occupants.each{
                def user = it.split("/")[-1] + "@" + jabberDomain
                def vCard = new VCard()
                vCard.load(jabber.connection, user)
                vCards << vCard
            }
        }

        vCards
    }


    void afterPropertiesSet() throws Exception {
        loadFromTable()
        connect()
    }
}
