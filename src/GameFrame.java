import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class GameFrame extends JFrame implements ActionListener
{
	JButton replay;
	GamePanel panel;
	public GameFrame() 
	{
		panel=new GamePanel();
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Snake");
		this.setResizable(false);
		this.setVisible(true);
		this.setSize(540,620);
		this.setLocationRelativeTo(null);
		
		replay=new JButton("Replay");
		replay.setFont(new Font("MV Boli", Font.BOLD, 30));
		replay.setBackground(new Color(255,255,255));
		replay.setForeground(Color.BLUE);
		replay.setFocusable(false);
		replay.addActionListener(this);
		this.add(replay,BorderLayout.SOUTH);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource()==replay)
		{
			this.dispose();
			new GameFrame();
		}
	}
}
