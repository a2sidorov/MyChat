package com.a2sidorov.mychat.view;

import javax.swing.*;
import java.awt.*;

public class IntroView {

    private JLabel labelAddress;
    private JLabel labelPort;
    private JLabel labelNickname;
    private JTextField textFieldAddress;
    private JTextField textFieldPort;
    private JTextField textFieldNickname;
    private JButton connectButton;

    private JPanel panelForm;
    private JFrame frame;

    public IntroView(JFrame frame) {

        this.frame = frame;

        /* Create UI emements */
        labelAddress = new JLabel("IP Address: ");
        labelPort = new JLabel("Port: ");
        labelNickname = new JLabel("Nickname: ");
        textFieldAddress = new JTextField(12);
        textFieldPort = new JTextField(12);
        textFieldNickname = new JTextField(12);
        connectButton = new JButton("Connect");

        /* Add UI elements to frame */
        panelForm = new JPanel(new GridBagLayout());
        Dimension dimension = new Dimension(300, 200);
        panelForm.setPreferredSize(dimension);
        panelForm.setMaximumSize(dimension);

        GridBagConstraints c = new GridBagConstraints();

        // IP Address label
        c.gridx = 0;
        c.gridy = 0;
        panelForm.add(labelAddress, c);

        // Port label
        c.gridy++;
        c.anchor = GridBagConstraints.LINE_END;
        panelForm.add(labelPort, c);

        // Nickname label
        c.gridy++;
        panelForm.add(labelNickname, c);

        // IP Address field
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        panelForm.add(textFieldAddress, c);

        // Port field
        c.gridy++;
        c.gridwidth = 2;
        panelForm.add(textFieldPort, c);

        // Nickname field
        c.gridy++;
        c.gridwidth = 2;
        panelForm.add(textFieldNickname, c);

        // Connect button
        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(20,0,0,0);
        panelForm.add(connectButton, c);
    }

    public void display() {
        JPanel contentPane = (JPanel) this.frame.getContentPane();
        contentPane.removeAll();
        contentPane.repaint();
        contentPane.add(panelForm);
        frame.setVisible(true);
    }

    public JTextField getTextFieldAddress() {
        return textFieldAddress;
    }


    public JTextField getTextFieldPort() {
        return textFieldPort;
    }

    public JTextField getTextFieldNickname() {
        return textFieldNickname;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public void setTextFieldAddress(JTextField textFieldAddress) {
        this.textFieldAddress = textFieldAddress;
    }

    public void setTextFieldPort(JTextField textFieldPort) {
        this.textFieldPort = textFieldPort;
    }

    public void setTextFieldNickname(JTextField textFieldNickname) {
        this.textFieldNickname = textFieldNickname;
    }


}
