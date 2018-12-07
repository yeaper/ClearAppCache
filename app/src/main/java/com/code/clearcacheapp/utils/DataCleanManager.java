package com.code.clearcacheapp.utils;

import android.content.Context;
import android.os.Environment;

import com.code.clearcacheapp.interfaces.InfoCallback;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 应用数据清除管理器
 */
public class DataCleanManager {

    private InfoCallback infoCallback; //删除信息回调

    public DataCleanManager(InfoCallback callback) {
        this.infoCallback = callback;
    }

    /**
     * 清除其他应用的所有数据
     */
    public boolean cleanOtherAppData(String packageName) {
        File file = new File("/data/data/" + packageName);
        System.out.println("包名为：" + packageName);

        if (file.exists()) {
            DataOutputStream os = null;
            Process p = null;

            try {
                if(infoCallback != null){
                    infoCallback.getInfo("清除内部数据命令 执行开始" + "\n");
                }
                p = Runtime.getRuntime().exec("su");// 获得root权限
                os = new DataOutputStream(p.getOutputStream());

                os.writeBytes("chmod 777 /data/data/" + packageName + "/ \n"); //拿到对应app包的读写执行权限
                os.flush();

                // data/data/packageName目录下要删除的文件夹
                List<String> list = new ArrayList<>();
                list.add("cache");
                list.add("databases");
                list.add("files");
                list.add("shared_prefs");
                for (int i = 0; i < list.size(); i++) {
                    os.writeBytes("rm -rif /data/data/" + packageName + "/" + list.get(i) + " \n");// 强制删除文件和文件夹
                    os.flush();
                    if(infoCallback != null){
                        infoCallback.getInfo("内部数据清除结束：" +
                                "/data/data/" + packageName + "/" + list.get(i) + "\n");
                    }
                }

                os.writeBytes("exit\n"); //退出命令
                os.flush();
                if(infoCallback != null){
                    infoCallback.getInfo("清除内部数据命令 执行结束" + "\n\n\n");
                }

                os.close();
                p.destroy();

                return true;

            } catch (IOException e) {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (p != null) {
                    p.destroy();
                }
            }
        }

        return false;
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public boolean delete(Context context, String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            if (infoCallback != null) {
                infoCallback.getInfo("文件或目录不存在：" + delFile + "\n");
            }
            return true;
        } else {
            if (file.isFile())
                return deleteSingleFile(context, delFile);
            else
                return deleteDirectory(context, delFile);
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private boolean deleteSingleFile(Context context, String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                if (infoCallback != null) {
                    infoCallback.getInfo("删除单个文件成功:" + filePath$Name + "\n");
                }
                return true;
            } else {
                if (infoCallback != null) {
                    infoCallback.getInfo("删除单个文件失败:" + filePath$Name + "\n");
                }
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private boolean deleteDirectory(Context context, String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            if (infoCallback != null) {
                infoCallback.getInfo("目录不存在：" + filePath + "\n");
            }
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(context, file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(context, file.getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            if (infoCallback != null) {
                infoCallback.getInfo("删除目录成功：" + filePath + "\n");
            }
            return true;
        } else {
            if (infoCallback != null) {
                infoCallback.getInfo("删除目录失败：" + filePath + "\n");
            }
            return false;
        }
    }

    /**
     * 获取sd卡目录
     *
     * @return
     */
    public String getSDPath() {
        File sdDir = null;
        //判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
            if (infoCallback != null) {
                infoCallback.getInfo("外部存储可用" + "\n");
            }
        } else {
            if (infoCallback != null) {
                infoCallback.getInfo("没有外部存储" + "\n");
            }
        }

        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return null;
        }
    }
}