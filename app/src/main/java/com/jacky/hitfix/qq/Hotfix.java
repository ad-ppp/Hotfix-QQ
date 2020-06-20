package com.jacky.hitfix.qq;

import android.app.Application;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Hotfix {


    /**
     * 1、获取到当前应用的PathClassloader;
     * <p>
     * 2、反射获取到DexPathList属性对象pathList;
     * <p>
     * 3、反射修改pathList的dexElements
     * 3.1、把补丁包patch.dex转化为Element[]  (patch)
     * 3.2、获得pathList的dexElements属性（old）
     * 3.3、patch+old合并，并反射赋值给pathList的dexElements
     */
    public static void fix(Application application, String patch) {
        File file = new File(patch);
        if (!file.exists()) {
            return;
        }
        List<File> files = new ArrayList<>();
        files.add(file);
        File dexOutputDir = application.getFilesDir();
        //1、
        ClassLoader classLoader = application.getClassLoader();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                NewClassLoaderInjector.inject(application, classLoader, files);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {

            //2、反射获取到DexPathList属性对象pathList;
            try {
                Field pathListField = ShareReflectUtil.findField(classLoader, "pathList");
                Object pathList = pathListField.get(classLoader);

                //3、反射修改pathList的dexElements
                //3.1、把补丁包patch.dex转化为Element[]  (patch)
                Method method = ShareReflectUtil.findMethod(pathList, "c",
                        List.class, File.class, List.class);

                ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();

                Object[] patchElments = (Object[]) method.invoke(null, files, dexOutputDir, suppressedExceptions);


                //3.2、获得pathList的dexElements属性（old）
                Field dexElementsFiled = ShareReflectUtil.findField(pathList, "dexElements");
                Object[] dexElements = (Object[]) dexElementsFiled.get(pathList);


                //3.3、patch+old合并，并反射赋值给pathList的dexElements

                Object newElements = Array.newInstance(dexElements.getClass().getComponentType(),
                        patchElments.length + dexElements.length);

                System.arraycopy(patchElments, 0, newElements, 0, patchElments.length);
                System.arraycopy(dexElements, 0, newElements, patchElments.length, dexElements.length);


                // 给DexPathList 中的 dexElements 重新赋值
                dexElementsFiled.set(pathList, newElements);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
