package Yootgame.source.ui;

import Yootgame.source.PlayGame;
import Yootgame.source.component.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Lobby extends JFrame {
	private BackgroundPanel panel = new BackgroundPanel();
	private JButton createRoomButton = new JButton("방 만들기");
	private ArrayList<JButton> roomButtons = new ArrayList<>();
	private JPanel roomPanel = new JPanel();
	private RoomSettingPanel roomSettingPanel;

	public Lobby() {
		this.setTitle("Lobby");
		this.setSize(1000, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		// Configure BackgroundPanel
		panel.setLayout(new BorderLayout());

		// Room List Panel
		roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(roomPanel);

		// Right Panel for Room List and Button
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(scrollPane, BorderLayout.CENTER);

		// Create Room Button
		createRoomButton.setPreferredSize(new Dimension(150, 30)); // Fix button size
		createRoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openRoomSettingPanel();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createRoomButton);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH); // Add button at the bottom of the right panel

		// Add Right Panel to Background
		panel.add(rightPanel, BorderLayout.EAST); // Place right panel on the right

		this.setContentPane(panel); // Set the main panel
		this.setVisible(true);
	}

	private void openRoomSettingPanel() {
		roomSettingPanel = new RoomSettingPanel(this);
		this.getContentPane().removeAll();
		this.add(roomSettingPanel);
		this.revalidate();
		this.repaint();
	}

	public void addRoom(String roomName, int pieceCount, int turnTime) {
		JButton roomButton = new JButton(roomName + " - " + pieceCount + "말, " + turnTime + "초");
		roomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PlayGame(pieceCount, turnTime);
				dispose(); // Close current window
			}
		});
		roomButtons.add(roomButton);
		roomPanel.add(roomButton);
		roomPanel.revalidate();
		roomPanel.repaint();

		// Return to Lobby
		this.getContentPane().removeAll();

		// Right Panel (Recreated to ensure layout remains)
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(new JScrollPane(roomPanel), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createRoomButton);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);

		panel.add(rightPanel, BorderLayout.EAST);
		this.setContentPane(panel);
		this.revalidate();
		this.repaint();
	}

	public JPanel getRoomPanel() {
		return roomPanel;
	}

	public JButton getCreateRoomButton() {
		return createRoomButton;
	}

	public JPanel getPanel() {
		return panel;
	}

	private static class BackgroundPanel extends JPanel {
		private Image backgroundImage;

		public BackgroundPanel() {
			try {
				// Adjust the path to match your project structure
				backgroundImage = new ImageIcon("src/main/java/Yootgame/img/backgroundFicture.png").getImage();
			} catch (Exception e) {
				System.err.println("Background image not found: " + e.getMessage());
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (backgroundImage != null) {
				Graphics2D g2d = (Graphics2D) g.create();

				// Enable smooth rendering
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				// Panel dimensions
				int panelWidth = getWidth();
				int panelHeight = getHeight();

				// Image dimensions
				int imgWidth = backgroundImage.getWidth(this);
				int imgHeight = backgroundImage.getHeight(this);

				// Calculate scale to fit the panel while maintaining aspect ratio
				double widthScale = (double) panelWidth / imgWidth;
				double heightScale = (double) panelHeight / imgHeight;
				double scale = Math.min(widthScale, heightScale); // Choose the smaller scale to fit both dimensions

				// Calculate new dimensions of the image
				int newWidth = (int) (imgWidth * scale);
				int newHeight = (int) (imgHeight * scale);

				// Position the image at the left side
				int x = 0; // Align to the left
				int y = (panelHeight - newHeight) / 2; // Center vertically

				// Draw the scaled image
				g2d.drawImage(backgroundImage, x, y, newWidth, newHeight, this);

				g2d.dispose(); // Dispose of the graphics context
			}
		}
	}

}
