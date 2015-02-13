package com.qints.sign;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import com.qints.util.StringResource;

public class SignerPanel extends JPanel {

	private final String titleContext = StringResource.getStringByLabel("SIGNER_LABEL");

	private JTabbedPane tabPane = new JTabbedPane();
	private JLabel titleLab = new JLabel(titleContext);

	public SignerPanel() {
		initCompo();
		initListener();
		setting();
	}

	private void setting() {
		setVisible(true);
	}

	private void initCompo() {

		tabPane.add(new SignerJarPanel(), "为jar签名");
		tabPane.add(new GenerateJksPanel(), "制作签名文件");

		setLayout(new MigLayout("insets 10","[grow]","[][grow]"));
		JPanel northPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		northPane.add(titleLab);
		add(northPane, "growx,wrap,h 30!");
		add(tabPane, "growx,growy,h 400");
	}

	private void initListener() {

	}

}
