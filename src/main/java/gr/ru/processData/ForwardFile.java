package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.dao.MesagaDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.Packs2Server.FileFromUser;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Gri on 26.09.2016.
 */
public class ForwardFile implements HandleTelegramm {
    private MesagaDAO mesagaDAO;
    private HashMapDB hashMapDB;

    @Override
    public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
        // Преобразование и проверка что данные верны
        FileFromUser fileTelega = validTele (packet) ;
        if (fileTelega==null || fileTelega.from!=ctxChanel.channel().attr(NettyServer.USER).get().getId() ){
            System.out.println("Validation of Mesaga - ERR!!!");
            return;
        }

        // Алгоритм обработки кусочка файла
        //
        //String fotoPath = "f" + fileTelega.from + "/f_" + fileTelega.rowid + "-" + fileTelega.pieceId + ".jpg";
        final String userFolder = "user_" + fileTelega.from;    // Папка для файлов юзера
        final String fileFolder = "f_" + fileTelega.rowid;      // Папка для кусочков конкретного файла
        final String piceName = "_" + fileTelega.pieceId;       //

        System.out.println("foto.length=" + fileTelega.foto.length + " fotoPath=" + userFolder +"/"+fileFolder+"/"+piceName );
        // Сохраняем на диск
        try {
            FileOutputStream fos = new FileOutputStream(userFolder +"/"+fileFolder+"/"+piceName);
            fos.write(fileTelega.foto);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Save file ERR!!!");
            return;
        }

        // Проверяем, все ли кусочки собраны
        File fp = new File(userFolder + "/" + fileFolder + "/");
        System.out.println("filePice saved. Current pices count="+ fp.list().length);
        if ( fp.list().length == fileTelega.piecesCount  ){

        }else if(fp.list().length > fileTelega.piecesCount ){
            System.out.println("ERR!!! Too much pices");
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
        }
        else{
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
