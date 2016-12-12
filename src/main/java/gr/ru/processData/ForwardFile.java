package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.dao.MesagaDAO;
import gr.ru.dao.Mesage;
import gr.ru.dao.Notific;
import gr.ru.dao.User;
import gr.ru.gutil;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client;
import gr.ru.netty.protokol.Packs2Client.ServerStat;
import gr.ru.netty.protokol.Packs2Server.FileFromUser;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

//import org.apache.commons. lang3.ArrayUtils;

/**
 * Created by Gri on 26.09.2016.
 */
public class ForwardFile implements HandleTelegramm {
    private static final Logger LOG = LogManager.getLogger(ForwardFile.class);
    private MesagaDAO mesagaDAO;
    private HashMapDB hashMapDB;

    @Override
    public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
        // Преобразование и проверка что данные верны
        FileFromUser fileTelega = validTele(packet);
        if (fileTelega == null || fileTelega.from != ctxChanel.channel().attr(NettyServer.USER).get().getId()) {
            LOG.error("Validation of file Mesaga - ERR!!!");
            return;
        }

        // Алгоритм обработки кусочка файла

        // 1. Сохраняем пришедший кусочек на диск
        final String autorFolder = "user_" + fileTelega.from;    // Папка для файлов юзера
        final String fileFolder = "f_" + fileTelega.rowid;      // Папка для кусочков конкретного файла
        final String piceName = "_" + fileTelega.pieceId;       //
        //LOG.debug("getfile: " + fileTelega.toString());
        LOG.debug("foto.length=" + fileTelega.foto.length + " fotoPath=" + autorFolder + "/" + fileFolder + "/" + piceName);
        // Сохраняем на диск
        File filePice = new File("fotos/" + autorFolder + "/" + fileFolder + "/" + piceName);
        filePice.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(filePice);
            fos.write(fileTelega.foto);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Save file ERR!!! \n" + e);
            return;
        }

        // 2. Оповещаем автора что кусочек принят и можно удалять (без сохранения в Нотификейшена в Юзере, //TODO Проверить: т.к. нестрашно если юзер пришлет тот же кусок еще раз)
        ServerStat serverStat = (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT);
        serverStat.typ = gutil.MSG_PICEFILE_ONSERVER;
        serverStat.sts = fileTelega.pieceId;
        serverStat.dat = fileTelega.rowid;
        ctxChanel.writeAndFlush(serverStat);


        // Проверяем, все ли кусочки собраны
        File fp = new File("fotos/" + autorFolder + "/" + fileFolder + "/");
        LOG.debug("filePice saved. Current pices count=" + fp.list().length);

        //TODO: Криво и косо. Байты собираются из буфера, копируются в Лист, потом обратно в массив байт. Надо проверять

        // Если в папке собрано нужное количество кусочков, собираем их в один файл / //TODO: А нахера??? Что бы было!!! ))
        if (fp.list().length == fileTelega.piecesCount) {
            // Создаем масив куда будем писать байты
            ArrayList<Byte> listBufer = new ArrayList<>(gutil.SETUP_SIZEOF_PICE * fileTelega.piecesCount);

            for (int i = 0; i < fileTelega.piecesCount; i++) {
                File piceFile = new File("fotos/" + autorFolder + "/" + fileFolder + "/_" + i);

                // Если файл есть, перегоняем его в байты
                if (piceFile.exists()) {
                    int size = (int) piceFile.length();
                    byte[] bytes = new byte[size];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(piceFile));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        listBufer.addAll(Arrays.asList(ArrayUtils.toObject(bytes)));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        LOG.error("Save Pic ERR!!! FileNotFoundException \n" + e);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOG.error("Save Pic ERR!!! IOException \n" + e);
                        return;
                    }

                } else {
                    LOG.error("!!!ERR Pice not found " + "fotos/" + autorFolder + "/" + fileFolder + "/_" + i);
                }
            }

            // байты всех файлов сложили в listBufer, записываем картинку в файл
            // Сохраняем файл с именем временной папки
            File fileJpg = new File("fotos/" + autorFolder + "/" + fileFolder + ".jpg");
            fileJpg.getParentFile().mkdirs();
            try {
                FileOutputStream fos = new FileOutputStream(fileJpg);
                fos.write(ArrayUtils.toPrimitive(listBufer.toArray(new Byte[listBufer.size()])));
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                LOG.error("Save Pic ERR!!! \n" + e);
                return;
            }


            /*
            Ресайзить будем на стороне клиента. ПОка ресайз коментим
             */
//            // делаем превьюху
//            BufferedImage previewImage;
//            try {
//                previewImage = Scalr.resize(ImageIO.read( fileJpg ), gutil.SETUP_PREVIEW_SIZE);
//            } catch (IOException e) {
//                e.printStackTrace();
//                LOG.error("Make preview ERR!!!/n" + e);
//                return;
//            }
//
//            // Сохраняем превьюху
//            File previewJpg = new File("fotos/"+ autorFolder +"/"+fileFolder+"_prew.jpg");
//            try {
//                ImageIO.write(previewImage, "jpg", previewJpg);
//            } catch (IOException e) {
//                e.printStackTrace();
//                LOG.error("Save Preview ERR!!!/n"+e);
//                return;
//            }

            /*
            В этот момент очевидно что файл целиком принят и сохранен. Оповещаем автора. Делаем сообщение для получателя
             */
            LOG.info("new file collected and saved on server " + "fotos/" + autorFolder + "/" + fileFolder + ".jpg");

            // Создаем оповещение для автора
            Notific notifORM = new Notific();
            notifORM.setLocalRowId(fileTelega.rowid);
            notifORM.setTime(System.currentTimeMillis());                    // Любой нотификейшн хранит время появления сообщения на сервере (надо для автора собщения)
            notifORM.setUserRecipient(ctxChanel.channel().attr(NettyServer.USER).get());
            notifORM.setStatus(gutil.MSG_ONSERVER);                            // ставим статус оповещению НА СЕРВЕРЕ
            // Сохраняем в Юзере (в авторе)
            ctxChanel.channel().attr(NettyServer.USER).get().getUnRecivedNotif().add(notifORM);
            // Перегоняем оповещение в нужный формат и отправляем автору сообщения
            ServerStat serverStat2 = notifORM.fillNettyPack((ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT));
            ctxChanel.writeAndFlush(serverStat2);        // Отправляем оповещение (БЕЗ слушателя)


            final Mesage msgORM = new Mesage();
            msgORM.setAutorId(fileTelega.from);

            msgORM.setLocalRowId(fileTelega.rowid);
            msgORM.setMesaga("" + fileJpg.getPath());
            msgORM.setMsgType((short) gutil.MSG_TYP_FOTO);
            msgORM.setTime(notifORM.getTime());
            // Поиск Юзера получателя сообщение
            User recipientUser = hashMapDB.getUser(fileTelega.to);        // Возможно НУЛЛ, будет проверено на этапе попытки отправить ему сообщение
            msgORM.setUserRecipient(recipientUser);

            //Получатель найден и вроде бы в сети
            if (recipientUser != null && recipientUser.getMapChanel() != null && recipientUser.getMapChanel().isActive()) {
                recipientUser.getUnRecivedMsg().add(msgORM);        // Сохраняем сообщение в получателе
                // Создаем и тут же заполняем новый пакет
                Packs2Client.MsgToUser msgNetty = msgORM.fillNettyPack((Packs2Client.MsgToUser) PacketFactory.produce(PacketFactory.MSG_TO_USER));
                recipientUser.getMapChanel().writeAndFlush(msgNetty);        // Отправляем сообщение (БЕЗ слушателя)
            }
            // Получатель НЕ в сети
            else {
                mesagaDAO.saveMesaga(msgORM, fileTelega.to);                    // сохраняем сообщение на сервере
            }


        } else if (fp.list().length > fileTelega.piecesCount) {
            LOG.error("ERR!!! Too much pices in folder " + "fotos/" + autorFolder + "/" + fileFolder + "/");
        }


//        // Если пришел файл, надо его сохранить, а имя файла записать в текст сообщения
//        if (msgTelega.msgtyp == gutil.MSG_TYP_FOTO){
//            if (msgTelega.foto.length>0 ){
//                String fotoPath = "foto/foto_" + System.currentTimeMillis() + ".jpg";
//                System.out.println("foto.length="+msgTelega.foto.length +" fotoPath="+fotoPath);
//                try {
//                    FileOutputStream fos = new FileOutputStream(fotoPath);
//                    fos.write(msgTelega.foto);
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("Save file ERR!!!");
//                    return;
//                }
//                msgTelega.msg = "[photo]"+fotoPath;
//
//            }else{
//                System.out.println("Wrong file size ERR!!!");
//                return;
//            }
//        }else{
//            System.out.println("inome simple text="+msgTelega.msg);
//        }


    }


    @Override
    public FileFromUser validTele(Packet packet) {

        FileFromUser msgTelega = (FileFromUser) packet;
        if (msgTelega.from == 0 || msgTelega.rowid == 0 || msgTelega.to == 0 ||
                msgTelega.piecesCount == 0 || //msgTelega.pieceId == 0 ||
                msgTelega.foto == null || msgTelega.foto.length == 0) {
            return null;
        } else {
            return msgTelega;
        }

    }


    public MesagaDAO getMesagaDAO() {
        return mesagaDAO;
    }

    public void setMesagaDAO(MesagaDAO mesagaDAO) {
        this.mesagaDAO = mesagaDAO;
    }

    public HashMapDB getHashMapDB() {
        return hashMapDB;
    }

    public void setHashMapDB(HashMapDB hashMapDB) {
        this.hashMapDB = hashMapDB;
    }


}
