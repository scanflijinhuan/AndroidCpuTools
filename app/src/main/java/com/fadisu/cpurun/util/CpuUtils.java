package com.fadisu.cpurun.util;


import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CpuUtils {

    private static final String TAG =  CpuUtils.class.getSimpleName();

    /**
     * 获取 CPU 名称
     *
     * @return
     */
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

        /**
          * Gets the number of cores available in this device, across all processors.
          * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
          * <p>
          * Source: http://stackoverflow.com/questions/7962155/
          *
          * @return The number of cores, or 1 if failed to get result
          */
        public static int getNumCpuCores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements java.io.FileFilter {
                @Override
                public boolean accept(java.io.File pathname) {
                    // Check if filename is "cpu", followed by a single digit number
                    if (java.util.regex.Pattern.matches("cpu[0-9]+", pathname.getName())) {
                            return true;
                        }
                    return false;
                    }
            }

        try {
                // Get directory containing CPU info
                java.io.File dir = new java.io.File("/sys/devices/system/cpu/");
                // Filter to only list the devices we care about
                java.io.File[] files = dir.listFiles(new CpuFilter());
                // Return the number of cores (virtual CPU devices)
                return files.length;
            } catch (Exception e) {
                // Default to return 1 core
                Log.e(TAG, "Failed to count number of cores, defaulting to 1", e);
                return 1;
            }
    }

    /**
     * Get cpu's current frequency
     * unit:KHZ
     * 获取cpu当前频率,单位KHZ
     *
     * @return
     */
    public static List<Integer> getCpuCurFreq() {
        List<Integer> results = new ArrayList<Integer>();
        String freq = "";
        FileReader fr = null;
        try {
            int cpuIndex = 0;
            Integer lastFreq = 0;
            while (true) {
                File file = new File("/sys/devices/system/cpu/cpu" + cpuIndex + "/");
                if (!file.exists()) {
                    break;
                }
                file = new File("/sys/devices/system/cpu/cpu" + cpuIndex + "/cpufreq/");
                if (!file.exists()) {
                    lastFreq = 0;
                    results.add(0);
                    cpuIndex++;
                    continue;
                }
                file = new File("/sys/devices/system/cpu/cpu" + cpuIndex + "/cpufreq/scaling_cur_freq");
                if (!file.exists()) {
                    results.add(lastFreq);
                    cpuIndex++;
                    continue;
                }
                fr = new FileReader(
                        "/sys/devices/system/cpu/cpu" + cpuIndex + "/cpufreq/scaling_cur_freq");
                BufferedReader br = new BufferedReader(fr);
                String text = br.readLine();
                freq = text.trim();
                lastFreq = Integer.valueOf(freq);
                results.add(lastFreq);
                fr.close();
                cpuIndex++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}