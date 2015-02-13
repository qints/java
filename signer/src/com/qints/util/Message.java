package com.qints.util;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Message {
	  public static void showMessage(Component compo,String context){
		  JOptionPane.showMessageDialog(compo, context, "¾¯¸æ", JOptionPane.WARNING_MESSAGE);
	  }
}
