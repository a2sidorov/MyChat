package com.a2sidorov.mychat;

import com.a2sidorov.mychat.controller.IntroController;
import com.a2sidorov.mychat.model.InputValidation;
import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.network.NetworkClient;
import com.a2sidorov.mychat.view.IntroView;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyChatClient extends JFrame {

    public NetworkClient networkClient;
    public static JTextArea textChat;
    public static JList listUsers;
    private JPanel mainPanel;


    public MyChatClient() {

        createFormView();

        //createMainView();

        setTitle("MyChat");
        setSize(600,500);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                /*
                try {
                    networkClient.disconnect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                */
                System.exit(0);
            }
        });

    }

    private void createFormView() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        getContentPane().add(mainPanel);

        JPanel dialogPanel = new JPanel();
        dialogPanel.setSize(new Dimension(200, 200));
        mainPanel.add(dialogPanel, BorderLayout.CENTER);

        JLabel labelDialog = new JLabel();
        labelDialog.setText("Enter a nickname:");
        dialogPanel.add(labelDialog);

        JTextField fieldDialog = new JTextField();
        fieldDialog.setPreferredSize(new Dimension(200, 20));
        dialogPanel.add(fieldDialog);

        JButton buttonDialog = new JButton("Save");
        buttonDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nickname = fieldDialog.getText().trim();
                if(InputValidation.isNicknameValid(nickname)) {
                    dialogPanel.removeAll();
                    dialogPanel.revalidate();
                    createMainView();
                    try {
                        networkClient = new NetworkClient();
                        networkClient.setNickname(nickname);
                        networkClient.connectToServer("localhost",1050 ); //TODO move params to networkClient config
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }

            }
        });
        mainPanel.add(buttonDialog);

    }

    private void createMainView() {
        mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        //East
        listUsers = new JList();
        listUsers.setPreferredSize(new Dimension(200, 0));
        listUsers.setBorder(BorderFactory.createTitledBorder("Connected Users"));
        mainPanel.add(listUsers, BorderLayout.EAST);

        //Center
        textChat = new JTextArea();
        textChat.setEditable(false);
        ((DefaultCaret) textChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); //auto scroll down to last message
        JScrollPane textChatSP = new JScrollPane(textChat);
        textChatSP.setBorder(BorderFactory.createTitledBorder("Chat"));
        mainPanel.add(textChatSP, BorderLayout.CENTER);

        //South
        JPanel panelInput = new JPanel(new BorderLayout());
        mainPanel.add(panelInput, BorderLayout.SOUTH);

        JTextField fieldInput = new JTextField();
        //fieldInput.setPreferredSize(new Dimension(500,15));
        panelInput.add(fieldInput, BorderLayout.CENTER);


        JButton buttonSend = new JButton("Send");
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String text = fieldInput.getText();
                networkClient.sendMessage(text);
                fieldInput.setText("");

            }
        });
        panelInput.add(buttonSend, BorderLayout.EAST);

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Settings settings = new Settings();
            IntroView introView = new IntroView();
            IntroController introController = new IntroController(settings, introView);
            introController.initView();
            introController.initController();
        });

    }

}
