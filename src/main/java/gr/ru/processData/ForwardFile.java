package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.dao.MesagaDAO;
import gr.ru.gutil;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.Packs2Server.FileFromUser;
import io.netty.channel.ChannelHandlerContext;
//import org.apache.commons. lang3.ArrayUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        FileFromUser fileTelega = validTele (packet) ;
        if (fileTelega==null || fileTelega.from!=ctxChanel.channel().attr(NettyServer.USER).get().getId() ){
            LOG.error("Validation of file Mesaga - ERR!!!");
            return;
        }

        // Алгоритм обработки кусочка файла
        //
        //String fotoPath = "f" + fileTelega.from + "/f_" + fileTelega.rowid + "-" + fileTelega.pieceId + ".jpg";
        final String  autorFolder = "user_" + fileTelega.from;    // Папка для файлов юзера
        final String fileFolder = "f_" + fileTelega.rowid;      // Папка для кусочков конкретного файла
        final String piceName = "_" + fileTelega.pieceId;       //

        LOG.debug("foto.length=" + fileTelega.foto.length + " fotoPath=" +  autorFolder +"/"+fileFolder+"/"+piceName );
        // Сохраняем на диск
        try {
            FileOutputStream fos = new FileOutputStream( autorFolder +"/"+fileFolder+"/"+piceName);
            fos.write(fileTelega.foto);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Save file ERR!!!");
            return;
        }

        // Проверяем, все ли кусочки собраны
        File fp = new File( autorFolder + "/" + fileFolder + "/");
        LOG.debug("filePice saved. Current pices count=" + fp.list().length);

        //TODO: Криво и косо. Байты собираются из буфера, копируются в Лист, потом обратно в массив байт. Надо проверять

        // Если в папке собрано нужное количество кусочков, собираем их в один файл
        if (fp.list().length == fileTelega.piecesCount) {
            // Создаем масив куда будем писать байты
            ArrayList<Byte> listBufer = new ArrayList<Byte>(gutil.SETUP_SIZEOF_PICE * fileTelega.piecesCount);

            for (int i = 0; i < fileTelega.piecesCount; i++) {
                File piceFile = new File( autorFolder + "/" + fileFolder + "/_" + i);

                // Если файл есть, перегоняем его в байты
                if (piceFile.exists()) {
                    int size = (int) piceFile.length();
                    byte[] bytes = new byte[size];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(piceFile));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        listBufer.addAll(Arrays.asList(  ArrayUtils.toObject(bytes) ));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        LOG.error("Save Pic ERR!!! FileNotFoundException /n"+e);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOG.error("Save Pic ERR!!! IOException /n"+e);
                        return;
                    }

                } else{
                    LOG.error("!!!ERR Pice not found " +  autorFolder + "/" + fileFolder + "/_" + i);
                }
            }

            // байты всех файлов сложили в listBufer, записываем картинку в файл
            // Сохраняем файл с именем временной папки
            try {
                FileOutputStream fos = new FileOutputStream( autorFolder +"/"+fileFolder+".jpg");
                fos.write(  ArrayUtils.toPrimitive(listBufer.toArray( new Byte[ listBufer.size()] ))   );
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                LOG.error("Save Pic ERR!!!/n"+e);
                return;
            }

        }else if(fp.list().length > fileTelega.piecesCount ){
            LOG.error("ERR!!! Too much pices in folder "+ autorFolder + "/" + fileFolder + "/");
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
