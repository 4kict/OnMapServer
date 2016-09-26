package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.dao.MesagaDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.Packs2Server.FileFromUser;
import io.netty.channel.ChannelHandlerContext;

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
