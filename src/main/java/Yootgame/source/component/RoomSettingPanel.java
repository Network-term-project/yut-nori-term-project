package Yootgame.source.component;


import Yootgame.source.ui.RoomConfigPage;
import Yootgame.source.ui.robbyPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RoomSettingPanel extends JPanel {
    private robbyPage parent;
    private ButtonGroup pieceGroup = new ButtonGroup();
    private ButtonGroup timeGroup = new ButtonGroup();
    private JButton createButton = new JButton("방 만들기");
    private JButton cancelButton = new JButton("취소");

    public RoomSettingPanel(robbyPage parent) {
        this.parent = parent;
        this.setLayout(new BorderLayout()); // Main layout for the panel

        // Right-Aligned Panel
        JPanel rightAlignedPanel = new JPanel();
        rightAlignedPanel.setLayout(new BoxLayout(rightAlignedPanel, BoxLayout.Y_AXIS));
        rightAlignedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        rightAlignedPanel.setOpaque(false);

        // "말 개수" Section
        JPanel piecePanel = new JPanel();
        piecePanel.setLayout(new BoxLayout(piecePanel, BoxLayout.Y_AXIS));
        piecePanel.setOpaque(false);

        JLabel pieceLabel = new JLabel("말 개수:");
        pieceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT); // Align to the right
        piecePanel.add(pieceLabel);

        JPanel pieceOptionsPanel = new JPanel();
        pieceOptionsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align options to the right
        pieceOptionsPanel.setOpaque(false);

        for (int i = 2; i <= 5; i++) {
            JRadioButton pieceOption = new JRadioButton(i + "개");
            pieceOption.setActionCommand(String.valueOf(i));
            pieceGroup.add(pieceOption);
            pieceOptionsPanel.add(pieceOption);
        }
        piecePanel.add(pieceOptionsPanel);

        // "턴당 시간" Section
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setOpaque(false);

        JLabel timeLabel = new JLabel("턴당 시간:");
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT); // Align to the right
        timePanel.add(timeLabel);

        JPanel timeOptionsPanel = new JPanel();
        timeOptionsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align options to the right
        timeOptionsPanel.setOpaque(false);

        int[] times = {15, 30, 45, 60};
        for (int time : times) {
            JRadioButton timeOption = new JRadioButton(time + "초");
            timeOption.setActionCommand(String.valueOf(time));
            timeGroup.add(timeOption);
            timeOptionsPanel.add(timeOption);
        }
        timePanel.add(timeOptionsPanel);

        // Add sections to the right-aligned panel
        rightAlignedPanel.add(piecePanel);
        rightAlignedPanel.add(Box.createVerticalStrut(10)); // Add spacing between sections
        rightAlignedPanel.add(timePanel);
        rightAlignedPanel.add(Box.createVerticalStrut(10)); // Add spacing before buttons

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right
        buttonPanel.setOpaque(false);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // parent (robbyPage)로 돌아가기
                Window window = SwingUtilities.getWindowAncestor(RoomSettingPanel.this);
                if (window != null) {
                    window.dispose();
                }
            }
        });
        buttonPanel.add(cancelButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pieceGroup.getSelection() != null && timeGroup.getSelection() != null) {
                    String selectedPiece = pieceGroup.getSelection().getActionCommand();
                    String selectedTime = timeGroup.getSelection().getActionCommand();
                    RoomConfigPage configPage = new RoomConfigPage(parent, parent.client);
                    configPage.setVisible(true);
                    Window window = SwingUtilities.getWindowAncestor(RoomSettingPanel.this);
                    if (window != null) {
                        window.dispose();
                    }
                }
            }
        });
        buttonPanel.add(createButton);

        // Add components to the main layout
        rightAlignedPanel.add(buttonPanel);
        this.add(rightAlignedPanel, BorderLayout.EAST); // Align the entire panel to the right
    }
}
