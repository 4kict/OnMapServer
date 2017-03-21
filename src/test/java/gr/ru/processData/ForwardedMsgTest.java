package gr.ru.processData;

import gr.ru.AbstractUnitilsBaseTest;
import gr.ru.dao.Mesage;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.PacketFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringBeanByType;

import java.util.Random;
import java.util.Set;

import static gr.ru.gutil.MSG_DELIVERED;
import static gr.ru.netty.protokol.PacketFactory.*;
import static gr.ru.netty.protokol.Packs2Server.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ForwardedMsgTest extends AbstractUnitilsBaseTest {

    @SpringBeanByType
    private UserDAO userDAO;
    @SpringBeanByType
    private ForwardedMsg forwardedMsg;
    @SpringBeanByType
    private UserCommand userCommand;
    @SpringBeanByType
    private RegUser regUser;
    @SpringBeanByType
    private UserDisconnect userDisconnect;

    ChannelHandlerContext ctxChanelVasia;
    ChannelHandlerContext ctxChanelLumumba;

    private Random rnd = new Random();
    // Регистрационные данные Лумумбы
    RegData lumumbaRegData;


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
        ctxChanelVasia = mock(ChannelHandlerContext.class);
        when(ctxChanelVasia.channel()).thenReturn(channel);

        User expectedLumum = new User();
        expectedLumum.setId(-111L);
        expectedLumum.setCity("Numbai");
        expectedLumum.setCountry("nigeria");
        expectedLumum.setHashkey(9876543321L);
        expectedLumum.setName("Lumumbu");

        Attribute<User> attributeLum = new AttributeMock<>();

        Attribute<Integer> attributeMock = new AttributeMock<>();
        //Attribute attributeLumSes = mock(Attribute.class);
//        when(attributeLumSes.get()).thenReturn(9988);
        Channel channelLum = mock(Channel.class);
        when(channelLum.attr(NettyServer.USER)).thenReturn(attributeLum);
        when(channelLum.attr(NettyServer.SESSION)).thenReturn(attributeMock);
        ctxChanelLumumba = mock(ChannelHandlerContext.class);
        when(ctxChanelLumumba.channel()).thenReturn(channelLum);

        // Регистрационные данные Лумумбы
        lumumbaRegData = (RegData) PacketFactory.produce(REG_DATA);
        lumumbaRegData.an_id = rnd.nextInt();
        lumumbaRegData.sesion = 0;
        lumumbaRegData.name = "Numbai"; // Логин
        //public String locale = ""; //
        //public long u_id; // Уникальный ID выданный сервером. - скорее всего ноль, иначе - юзер меняет рег данные
        lumumbaRegData.an_id = 999; // хэш от уникального ID андройда
        lumumbaRegData.dev_id = 998; // хэш от уникального ID устройства
        lumumbaRegData.key_id = 997; // уникальный (случайный) ID
        lumumbaRegData.lat = 1;     // Координаты
        lumumbaRegData.lon = 2;
        lumumbaRegData.status = 1;
        lumumbaRegData.ico = 1; // �?конка
        lumumbaRegData.qad = 1; // Номер квадрата
    }

    @Test
    @DataSet({"/datasets/TemplateDataSet.xml"})
    @ExpectedDataSet({"/datasets/expected/step1_sendMessaga.xml"})
    public void _1StepTest() throws Exception {
        // Вася отправляет сообщение
        MsgFromUser msgFromUser = (MsgFromUser) PacketFactory.produce(MSG_FROM_USER);
        msgFromUser.from = -111L;
        msgFromUser.to = -112L;
        msgFromUser.msg = "someMessage from Vasia to Mumbai";
        msgFromUser.msgtyp = 1;
        msgFromUser.sesion = 2;
        msgFromUser.rowid = 333;
        forwardedMsg.handle(ctxChanelVasia, msgFromUser);
    }

    @Test
    @ExpectedDataSet({"/datasets/expected/step2_reciveMessaga.xml"})
    public void _2StepTest() {
        // Лумумба регистрируется
        regUser.handle(ctxChanelLumumba, lumumbaRegData);

        // Лумумба сообщает что получил сообещние
        CmdFromUser command = (CmdFromUser) PacketFactory.produce(CMD_FROM_USER);
        command.cmd = MSG_DELIVERED;
        command.dat = 333;        // Номер доставленного сообщения в системе автора
        command.dat2 = -111L;        // ИД автора сообщения / получателя нотификейшена
        userCommand.handle(ctxChanelLumumba, command);

        // Лумумба дисконектится
        userDisconnect.handle(ctxChanelLumumba, null);
    }

    @Test
    @DataSet({"/datasets/UnrecivedMessagesDataSet.xml"})
    public void unrecivedMessagesTest() {
        regUser.handle(ctxChanelLumumba, lumumbaRegData);
        User userLumuba = ctxChanelLumumba.channel().attr(NettyServer.USER).get();
        Set<Mesage> unrecived = userLumuba.getUnRecivedMsg();
        for (Mesage mesage : unrecived) {
            Assert.assertTrue(mesage.getId() == -221L || mesage.getId() == -222L);
        }
    }

    @Test
    public void someTest() throws Exception {
        User user = userDAO.getUser(-111L);
        Assert.assertEquals(-111L, (long) user.getId());
    }
}