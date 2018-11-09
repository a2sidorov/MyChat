package com.a2sidorov.mychat;

import com.a2sidorov.mychat.network.NetworkClient;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;

public class MychatClient extends JFrame {

    public NetworkClient client;
    public static JTextArea textChat;
    public static JList listUsers;
    private JPanel panelMain;


    public MychatClient() {

        createDialogView();

        //createMainView();

        setTitle("MyChat");
        setSize(600,500);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.disconnect();
                System.exit(0);
            }
        });

    }

    private void createDialogView() {
        panelMain = new JPanel();
        getContentPane().add(panelMain);

        JLabel labelDialog = new JLabel();
        labelDialog.setText("Enter a nickname:");
        panelMain.add(labelDialog);

        JTextField fieldDialog = new JTextField();
        fieldDialog.setPreferredSize(new Dimension(200, 20));
        panelMain.add(fieldDialog);

        JButton buttonDialog = new JButton("Save");
        buttonDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = fieldDialog.getText().trim();
                if(InputValidation.isValidNickname(nickname)) {
                    NetworkClient.setNickname(nickname);
                    panelMain.removeAll();
                    panelMain.revalidate();
                    createMainView();
                    client = new NetworkClient("localhost", 1050);
                    client.connectToServer();
                }
            }
        });
        panelMain.add(buttonDialog);

    }



    private void createMainView() {
        panelMain = new JPanel(new BorderLayout());
        getContentPane().add(panelMain);

        //East
        listUsers = new JList();
        listUsers.setPreferredSize(new Dimension(200, 0));
        listUsers.setBorder(BorderFactory.createTitledBorder("Connected Users"));
        panelMain.add(listUsers, BorderLayout.EAST);

        //Center
        textChat = new JTextArea();
        textChat.setEditable(false);
        ((DefaultCaret) textChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); //auto scroll down to last message
        JScrollPane textChatSP = new JScrollPane(textChat);
        textChatSP.setBorder(BorderFactory.createTitledBorder("Chat"));
        panelMain.add(textChatSP, BorderLayout.CENTER);

        //South
        JPanel panelInput = new JPanel(new BorderLayout());
        panelMain.add(panelInput, BorderLayout.SOUTH);

        JTextField fieldInput = new JTextField();
        //fieldInput.setPreferredSize(new Dimension(500,15));
        panelInput.add(fieldInput, BorderLayout.CENTER);

        JButton buttonSend = new JButton("Send");
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = fieldInput.getText();
                client.send(text);
                fieldInput.setText("");
            }
        });
        panelInput.add(buttonSend, BorderLayout.EAST);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MychatClient().setVisible(true);

        });
    }

}
