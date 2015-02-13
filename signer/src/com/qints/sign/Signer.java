package com.qints.sign;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.qints.util.StringResource;

public class Signer extends JFrame {
	
	private int width = 600;
	private int height = 500;

	public Signer() {
		setTitle(StringResource.getStringByLabel("APPLICATION_NAME"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		setLayout(new BorderLayout());
		add(new SignerPanel());
		setSize(width, height);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace();
				}
				new Signer();
			}
		});
	}
}
