package com.example.examplemodul;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements UncaughtExceptionHandler
{
	static public void setCrashHandler()
	{
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
	}

    private UncaughtExceptionHandler m_defaultUEH;


    public CrashHandler()
    {
        this.m_defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e)
    {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String strStacktrace = result.toString();
        printWriter.close();


		createLogFile(strStacktrace);

        m_defaultUEH.uncaughtException(t, e);
    }

	private void createLogFile(String strStacktrace)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss");
		String strFile = ApplicationTest.getInstance().getExternalFilesDir(null).getAbsolutePath()+"/crashes";
		File f = new File(strFile);
		f.mkdirs();
		strFile += "/"+df.format(new Date(System.currentTimeMillis()))+".txt";

		try
		{
			FileOutputStream fos = new FileOutputStream(strFile);
			fos.write(strStacktrace.getBytes(), 0, strStacktrace.length());
			fos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}