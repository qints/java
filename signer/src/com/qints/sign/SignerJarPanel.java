package com.qints.sign;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.miginfocom.swing.MigLayout;

import com.qints.util.Message;
import com.qints.util.StringResource;

/**
 * 为jar包签名的界面
 * @author qints
 * @data 2014年8月15日上午11:01:41
 * @version 1.0
 */
public class SignerJarPanel extends JPanel{
	
	//签名文件路径
	private JLabel jksFilePathLabel = new JLabel(StringResource.getStringByLabel("JKS_FILE_PATH"));
	private JTextField jksFilePathField = new JTextField(30);
	private JButton selectJksFileBtn = new JButton("...");
	
	//签名文件别名
	private JLabel jksAliasLabel = new JLabel(StringResource.getStringByLabel("JKS_ALIAS_LABEL"));
	private JTextField jksAliasField = new JTextField(30);
	
	//要签名的jar包
	private JLabel jarFilePathLabel = new JLabel(StringResource.getStringByLabel("JAR_FILE_PATH"));
	private JTextField jarFilePathField = new JTextField(30);
	private JButton selectJarFileBtn = new JButton("...");
	
	//输出结果
	private JTextArea reslutArea = new JTextArea(20, 30);
	
	//动作按钮
	private JButton signeBtn = new JButton(StringResource.getStringByLabel("SIGN"));
	private JButton helpBtn = new JButton(StringResource.getStringByLabel("HELP"));
	
	//输入字符串
	PrintWriter pw = null;
	private StringBuffer userInputString = new StringBuffer();
	
	public SignerJarPanel() {
		initCompo();
		initListener();	}

	private void initCompo() {
		
		reslutArea.setToolTipText("此处显示输出结果");
		
		setLayout(new MigLayout("insets 10","[right,grow][center,150!][left,grow]","20[]15[]15[]20[grow]20[30!,bottom]"));
		
		add(jksFilePathLabel,"");
		add(jksFilePathField,"");
		add(selectJksFileBtn,"w 25!,wrap");
		
		add(jksAliasLabel,"");
		add(jksAliasField,"wrap");
		
		add(jarFilePathLabel,"");
		add(jarFilePathField,"");
		add(selectJarFileBtn,"w 25!,wrap");
		
		add(new JScrollPane(reslutArea),"span 3,growx,growy,wrap");
		
		add(signeBtn,"span 3,split 2,center");
		add(helpBtn,"center");
		
		//初始化组件内容
		Preferences pref = getPref();
		jksFilePathField.setText(pref.get("jks.file.path", ""));
		jksFilePathField.setToolTipText(pref.get("jks.file.path", ""));
		jksAliasField.setText(pref.get("jks.alias", ""));
		jksAliasField.setToolTipText(pref.get("jks.alias", ""));
		jarFilePathField.setText(pref.get("jar.file.path", ""));
		jarFilePathField.setToolTipText(pref.get("jar.file.path", ""));
	}

	private void initListener() {
		
		selectJksFileBtn.addActionListener(signFileListener);
		selectJarFileBtn.addActionListener(signFileListener);
		signeBtn.addActionListener(signBtnListener);
		reslutArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char c = e.getKeyChar();
				if(c == '\n'){
					if(pw != null){
						try {
							System.out.println(userInputString.toString());
							pw.println(userInputString.toString());
							pw.flush();
							userInputString.setLength(0);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}else{
					userInputString.append(c);
				}
			}
		});
	}
	
	//签名动作相应事件
	private ActionListener signBtnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String jksFilePath = jksFilePathField.getText();
			String jksAlias = jksAliasField.getText();
			String jarFilePath = jarFilePathField.getText();
			
			if(jksFilePath.isEmpty()){
				showMessage("请选择jks签名文件！");
				return ;
			}
			if(jksAlias.isEmpty()){
				showMessage("请输入签名文件别名！");
				return ;
			}
			if(jarFilePath.isEmpty()){
				showMessage("请选择jar签名文件！");
				return ;
			}
			
			final StringBuffer cdExc = new StringBuffer();
			final StringBuffer signExc = new StringBuffer();
			cdExc.append("").append(findSignToolLab()).append("");
			
			signExc.append("jarsigner -keystore \"").append(jksFilePath).append("\" ");
			signExc.append("\"").append(jarFilePath).append("\" ");
			signExc.append(jksAlias);
			System.out.println(cdExc.toString());
			System.out.println(signExc.toString());
	        new Thread (){
	        	public void run(){
	        		Process pro = null;
	                try {
	                    pro = Runtime.getRuntime().exec(signExc.toString(),null,new File(cdExc.toString()));
	                    InputStream is = pro.getInputStream();
	                    InputStream isE = pro.getErrorStream();
	                    pw = new PrintWriter(pro.getOutputStream());
	                    StreamGobbler outputGobbler = new StreamGobbler(is);
	                    StreamGobbler errorGobbler = new StreamGobbler(isE);
	                    outputGobbler.start();
	                    errorGobbler.start();
	                    pro.waitFor();
	                  } catch (Exception e1) {
	                    e1.printStackTrace();
	          		}finally{
	          			pro.destroy();
	          		}
	        	}
	        }.start();
		}
	};
	
	//选择文件按钮的监听事件
	private ActionListener signFileListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Preferences pre = getPref();
			JFileChooser fileChooser = new JFileChooser();
			if(e.getSource().equals(selectJksFileBtn)){
				String rememberPath = pre.get("jks.file.path","");
				File rememberFile = new File("\\");
				if(!rememberPath.equals("")){
					rememberFile = new File(rememberPath).getParentFile();
				}
				fileChooser.setCurrentDirectory(rememberFile);
				fileChooser.setFileFilter(new SignFileFilter(".jks"));
			}else if(e.getSource().equals(selectJarFileBtn)){
				String rememberPath = pre.get("jar.file.path","");
				File rememberFile = new File("\\");
				if(!rememberPath.equals("")){
					rememberFile = new File(rememberPath).getParentFile();
				}
				fileChooser.setCurrentDirectory(rememberFile);
				fileChooser.setFileFilter(new SignFileFilter(".jar"));
			}
			int rel = fileChooser.showOpenDialog(SignerJarPanel.this);
			if(rel == JFileChooser.APPROVE_OPTION){
				if(e.getSource().equals(selectJksFileBtn)){
					String tempFilePath = fileChooser.getSelectedFile().getAbsolutePath();
					String tempFileName = fileChooser.getSelectedFile().getName();
					try {
						tempFileName = tempFileName.substring(0,tempFileName.indexOf("."));
					} catch (Exception e2) {
						tempFileName = fileChooser.getSelectedFile().getName();
					}
					jksFilePathField.setText(tempFilePath);
					jksFilePathField.setToolTipText(tempFilePath);
					jksAliasField.setText(tempFileName);
					pre.put("jks.file.path", tempFilePath);
					pre.put("jks.alias",tempFileName);
				}else if(e.getSource().equals(selectJarFileBtn)){
					String tempFilePath = fileChooser.getSelectedFile().getAbsolutePath();
					jarFilePathField.setText(tempFilePath);
					jarFilePathField.setToolTipText(tempFilePath);
					pre.put("jar.file.path", tempFilePath);
				}
			}
		}
	};
	
	//选择文件的过滤器
	private class SignFileFilter extends FileFilter {
		String filtPattern = "";
		
		public SignFileFilter(String pattern){
			this.filtPattern = pattern;
		}
		
		@Override
		public String getDescription() {
			return null;
		}
		@Override
		public boolean accept(File f) {
			if(f.isFile()){
				if(f.getName().toLowerCase().endsWith(filtPattern)){
					return true;
				}else{
					return false;
				}
			}
			return true;
		}
	}
	
	  private class StreamGobbler extends Thread {
			private InputStream is;
			public StreamGobbler(InputStream is) {
				this.is = is;
			}

			public void run() {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
		        byte[] data = new byte[4096];  
		        int count = -1;  
		        try {
					while((count = is.read(data,0,4096)) != -1) {
						outStream.write(data, 0, count);
						String temp = new String(outStream.toByteArray(),"gbk");
						insertRelArea(reslutArea,temp);
						reslutArea.setCaretPosition(reslutArea.getText().length());
					} 
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	  
	private Preferences getPref(){
		return Preferences.userNodeForPackage(SignerJarPanel.class);
	}
	
	protected String findSignToolLab(){
		String[] lab = System.getProperty("java.library.path").split(";");
		return lab[0];
	}
	
	private void showMessage(String context){
		Message.showMessage(this, context);
	}
	
	private void insertRelArea(JTextArea jta, String str){
		Document doc = jta.getDocument();
		if(doc != null){
			try {
				doc.insertString(doc.getLength(), str, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
}
