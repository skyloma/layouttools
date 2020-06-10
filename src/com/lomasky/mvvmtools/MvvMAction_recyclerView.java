package com.lomasky.mvvmtools;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiUtilBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MvvMAction_recyclerView extends BaseGenerateAction
{
    protected PsiClass mClass;
    private PsiElementFactory mFactory;
    private Project project;
    private String varName;
    private String packageName;
    private String modelPath;

    public MvvMAction_recyclerView()
    {
        super(null);
    }

    public MvvMAction_recyclerView(CodeInsightActionHandler handler)
    {
        super(handler);
    }

    protected boolean isValidForClass(PsiClass targetClass)
    {
        return super.isValidForClass(targetClass);
    }

    public boolean isValidForFile(Project project, Editor editor, PsiFile file)
    {
        return super.isValidForFile(project, editor, file);
    }

    public void actionPerformed(AnActionEvent event)
    {
        this.project = ((Project)event.getData(PlatformDataKeys.PROJECT));
        Editor editor = (Editor)event.getData(PlatformDataKeys.EDITOR);
        PsiFile mFile = PsiUtilBase.getPsiFileInEditor(editor, this.project);
        String mFilePath = ((VirtualFile)event.getData(PlatformDataKeys.VIRTUAL_FILE)).getPath();
        if (mFilePath.contains("src")) {
            this.modelPath = mFilePath.substring(0, mFilePath.indexOf("src"));
        }

        this.mClass = getTargetClass(editor, mFile);

        this.packageName = ((PsiJavaFileImpl)mFile).getPackageName();

        this.packageName = new StringBuilder().append(this.packageName).append(".").append(this.mClass.getName()).toString();

        if (this.modelPath != null) {

            int yn = Messages.showYesNoDialog(this.project, "是否使用数据绑定", "生成数据视图", Messages.getQuestionIcon());
            if (yn ==0)   createFile(this.project );

            else if(yn ==1 ) createFileBinding(this.project );
        }
    }

    private void sayHello(Project project)
    {


        Messages.showMessageDialog(project, "布局文件生成ing...请稍等！", "好消息",
                Messages.getInformationIcon());
        if (project != null){





            ProjectView.getInstance(project).refresh();
        }

    }

    private void createFileBinding(Project project )
    {
        File path = new File(new StringBuilder().append(this.modelPath).append("/src/main/res/layout").toString());
        if (!path.exists()) {
            path.mkdirs();
        }




        PsiField[] allFields =   mClass.getAllFields();

        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\n");
        sb.append("<!--复制下面注释的代码到string.xml-->");
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");

//
        for (PsiField field : allFields){

            String text = field.getText();
            String example = "";


            if (text.startsWith("//")) {
                try {
                    example = text.substring(2, text.indexOf("\n"));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (example.equals(""))
                sb.append("<!--<string name=\"tx_"+field.getName()+"\">"+field.getName()+"</string>-->").  append("\n");
            else
                sb.append("<!--<string name=\"tx_"+field.getName()+"\">"+example+"</string>-->")  .append("\n");



        }
        sb.append("\n");
        sb.append("\n");

        sb.append("<layout>\n" +
                "    <data>\n" +
                "        <variable\n" +
                "            name=\"data\"\n" +
                "            type=\""+this.packageName+"\" />\n" +
                "    </data>").append("\n");


        sb.append("<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "        xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "        xmlns:tools=\"http://schemas.android.com/tools\"\n" +
                "        android:orientation=\"vertical\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\">").append("\n");
        sb.append("    <TableLayout\n" +
                "            android:stretchColumns=\"1\"\n" +
                "             android:layout_marginHorizontal=\"16dp\"\n" +
                "            android:divider=\"@drawable/list_divider\"\n" +
                "            android:showDividers=\"middle\"\n" +
                "            android:layout_width=\"match_parent\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            >").append("\n");



        bindingLayout(sb, allFields);

        sb.append(" </TableLayout>").append("\n");


        sb.append("</LinearLayout>").append("\n");
        sb.append("</layout>").append("\n");


        File file = new File(path, new StringBuilder().append(this.mClass.getName().toLowerCase()).append("_item.xml").toString());
        try
        {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream out = new FileOutputStream(file);
            out.write(sb.toString().getBytes("UTF-8"));

            out.close();
            if (file.exists()) {
                sayHello(project);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void bindingLayout(StringBuilder sb, PsiField[] fFields) {
        for (PsiField field : fFields){

            String text = field.getText();
            String example = "";


            if (text.startsWith("//")) {
                try {
                    example = text.substring(2, text.indexOf("\n"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            sb.append("<TableRow>").append("\n");
            sb.append("<TextView\n" +
                    "            android:layout_width=\"wrap_content\"\n" +
                    "            android:layout_height=\"wrap_content\"\n" +
                    "            tools:text=\""+example+"\"\n" +
                    "            android:text=\"@{data."+field.getName()+"}\" />").append("\n");



            sb.append("</TableRow>").append("\n");
        }


    }








    private void createLayout(StringBuilder sb, PsiField[] fFields) {




        for (PsiField field : fFields){

            String text = field.getText();
            String example = "";


            if (text.startsWith("//")) {
                try {
                    example = text.substring(2, text.indexOf("\n"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            sb.append("<TableRow>").append("\n");
            sb.append("<TextView\n" +
                    "            android:id=\"@+id/textView"+field.getName()+"\"\n" +
                    "            android:layout_width=\"wrap_content\"\n" +
                    "            android:layout_height=\"wrap_content\"\n" +
                    "            tools:text=\""+example+"\" />").append("\n");


             sb.append("</TableRow>").append("\n");

        }





    }



    private void createFile(Project project )
    {
        File path = new File(new StringBuilder().append(this.modelPath).append("/src/main/res/layout").toString());
        if (!path.exists()) {
            path.mkdirs();
        }




        PsiField[] allFields =   mClass.getAllFields();

        StringBuilder sb = new StringBuilder();


        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\n");
        sb.append("<!--复制下面注释的代码到string.xml-->").append("\n").append("\n").append("\n");


        for (PsiField field : allFields){

            String text = field.getText();
            String example = "";


            if (text.startsWith("//")) {
                try {
                    example = text.substring(2, text.indexOf("\n"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (example.equals(""))
                sb.append("<!--<string name=\"tx_"+field.getName()+"\">"+field.getName()+"</string>-->").append("\n");
            else
                sb.append("<!--<string name=\"tx_"+field.getName()+"\">"+example+"</string>-->").append("\n");



        }
        sb.append("\n");
        sb.append("\n");
        sb.append("<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "        xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "        xmlns:tools=\"http://schemas.android.com/tools\"\n" +
                "        android:orientation=\"vertical\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\">").append("\n");


        sb.append("    <TableLayout\n" +
                "            android:stretchColumns=\"1\"\n" +
                "             android:layout_marginHorizontal=\"16dp\"\n" +
                "            android:divider=\"@drawable/list_divider\"\n" +
                "            android:showDividers=\"middle\"\n" +
                "            android:layout_width=\"match_parent\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            >").append("\n");



        createLayout(sb, allFields);

        sb.append(" </TableLayout>").append("\n");


        sb.append("</LinearLayout>").append("\n");








        File file = new File(path, new StringBuilder().append(this.mClass.getName().toLowerCase()).append("item.xml").toString());
        try
        {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream out = new FileOutputStream(file);
            out.write(sb.toString().getBytes("UTF-8"));
            out.close();

            if (file.exists()) {
                sayHello(project);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}