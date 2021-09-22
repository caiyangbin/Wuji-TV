package com.admin.libcommon.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProcessUtil {

    public static final String VIDTV = "/VidTV";

    public static final String MNT = "/mnt";

    private final static String TIME_FORMAT = "hhmmss";

    public static void mount(String path, String mountPath) {

        Log.i("mount", "target " + path + "mountPath");
        upgradeRootPermission("mount " + path + " " + mountPath);
    }

    public static void unmount(String mountPath) {
        Log.i("mount", "target " + mountPath + "mountPath");
        upgradeRootPermission("umount " + mountPath);
    }

    public static void link(String path, String linkPath) {
        linkPath = MNT + linkPath;

        Log.i("mount", "target " + path + " linkPath" + linkPath);

        upgradeRootPermission("ln -s " + path + " " + linkPath);
    }

    /**
     * 检测 vidtv是否存在 存在返回/VidTV000000
     * 
     * @param path
     * @return String
     */
    public static String checkVidTVPath(String path) {
        String[] commands = { "sh", "-c", "" };
        String checPath = "busybox ls " + path + VIDTV + "*";
        Log.i("mount", "checPath " + checPath);
        commands[2] = checPath;
        String vidTVPath = ProcessUtil.do_execs(commands);
        if (vidTVPath != null) {
            File file = new File(vidTVPath);
            String name = file.getName();
            return "/" + name;
        }
        return vidTVPath;
    }

    /**
     * 创建 vidtv 文件夹 并且返回当前文件/VidTV889898
     * 
     * @param path
     * @return
     */
    public static String touchVidTVPath(String path) {
        // 创建
        String date = getCurrentTime();
        String folder = VIDTV + date;
        path = path + folder;
        ProcessUtil.upgradeRootPermission("touch " + path);
        Log.i("mount", "VidTV " + path);
        return folder;
    }

    /**
     * 删除 mnt 下的路径
     * 
     * @param path
     */
    public static void rmMntpath(String path) {
        path = MNT + path;
        Log.i("mount", "rm " + path);
        ProcessUtil.upgradeRootPermission("rm " + path);
    }

    /**
     * 创建 vidtv 文件夹 并且返回当前文件/VidTV-889898
     * 
     * @param path
     * @return
     */
    public static String mkdirMntPath(String path) {
        // 创建
        path = MNT + path;
        ProcessUtil.upgradeRootPermission("mkdir " + path);
        Log.i("mount", "mnt " + path);
        return path;
    }

    public static void cleanMnt() {
        String[] commands = { "sh", "-c", "" };
        String checPath = "busybox ls " + MNT + VIDTV + "*";
        commands[2] = checPath;
        List<String> vidTVPaths = ProcessUtil.do_execss(commands);
        for (int i = 0; i < vidTVPaths.size(); i++) {
            String vidtv = vidTVPaths.get(i);
            Log.i("mount", "cleanMnt " + vidtv);
            ProcessUtil.upgradeRootPermission("rm " + vidtv);
        }
    }

    /*
     * 获取当前时间
     */
    public static String getCurrentTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String date = DateFormat.format(TIME_FORMAT, calendar).toString();
        return date;
    }

    /**
     * 获取sdcard 路径
     */
    public static String getSDPath() {

        File file = Environment.getExternalStorageDirectory();
        if (file != null)
            return file.getAbsolutePath();
        return null;
    }

    /**
     * 获得 root 权限 执行shell 命令
     * 
     * @param cmd
     * @return
     */
    public static boolean upgradeRootPermission(String cmd) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 执行shell 命令
     * 
     * @param cmd
     * @return string
     */
    public static String do_exec(String cmd) {
        String line = null;
        BufferedReader in = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmd);
            // 获取返回值
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            line = in.readLine(); // 读取一行
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                p.destroy();
            } catch (Exception e) {
            }
        }
        Log.i("mount", "line " + line);
        return line;
    }

    /**
     * 执行shell 命令
     * 
     * @return string
     */
    public static String do_execs(String[] cmds) {
        String line = null;
        BufferedReader in = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmds);
            // 获取返回值
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            line = in.readLine(); // 读取一行
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                p.destroy();
            } catch (Exception e) {
            }
        }
        Log.i("mount", "line " + line);
        return line;
    }

    /**
     * 执行shell 命令
     * 
     * @return string
     */
    public static List<String> do_execss(String[] cmds) {
        String line = null;
        BufferedReader in = null;
        Process p = null;
        List<String> strings = new ArrayList<String>();
        try {
            p = Runtime.getRuntime().exec(cmds);
            // 获取返回值
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // line = in.readLine(); //读取一行
            while ((line = in.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    Log.i("mount", "line " + line);
                    strings.add(line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                p.destroy();
            } catch (Exception e) {
            }
        }

        return strings;
    }
}
