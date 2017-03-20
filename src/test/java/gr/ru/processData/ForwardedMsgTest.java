package gr.ru.processData;

import gr.ru.AbstractUnitilsBaseTest;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.PacketFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringBeanByType;

import static gr.ru.netty.protokol.PacketFactory.MSG_FROM_USER;
import static gr.ru.netty.protokol.Packs2Server.MsgFromUser;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by
 */
@DataSet({"/datasets/TemplateDataSet.xml"})
public class ForwardedMsgTest extends AbstractUnitilsBaseTest {

    @SpringBeanByType
    private UserDAO userDAO;
    @SpringBeanByType
    private ForwardedMsg forwardedMsg;

    ChannelHandlerContext ctxChanel;

    public void init() {
        User expectedUser = new User();
        expectedUser.setId(-111L);
        expectedUser.setCity("Moskaw");
        expectedUser.setCountry("russia");
        expectedUser.setHashkey(123456789L);
        expectedUser.setName("Vasia");
        Attribute attribute = mock(Attribute.class);
        when(attribute.get()).thenReturn(expectedUser);
        Channel channel = mock(Channel.class);
        when(channel.attr(NettyServer.USER)).thenReturn(attribute);
        ctxChanel = mock(ChannelHandlerContext.class);
        when(ctxChanel.channel()).thenReturn(channel);
    }

    @Test
    @ExpectedDataSet({"/datasets/expected/TemplateDataSet.xml"})
    public void handle() throws Exception {
        MsgFromUser msgFromUser = (MsgFromUser) PacketFactory.produce(MSG_FROM_USER);
        msgFromUser.from = -111L;
        msgFromUser.to = -112L;
        msgFromUser.msg = "someMessage from Vasia to Mumbai";
        msgFromUser.msgtyp = 1;
        msgFromUser.sesion = 2;
        msgFromUser.rowid = 3;
        forwardedMsg.handle(ctxChanel, msgFromUser);
    }

    @Test
    public void someTest() throws Exception {
        User user = userDAO.getUser(-111L);
        Assert.assertEquals(-111L, (long) user.getId());
    }
}