import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignalTraceurSwing extends JFrame {

    private String binaryString;
    private String selectedCode;
    
    // méthode pour valider que la chaine binaire contient juste des 0 et des 1
    private boolean isValidBinaryString(String input) {
        return input.matches("[01]+");
    }
     
     SignalTraceurSwing() {
        setTitle("Signal Traceur");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        boolean isValidInput = false;
        //buttons pour basculer sur les codes
        JButton nrzButton = new JButton("NRZ");
        JButton nrziButton = new JButton("NRZI");
        JButton manchesterButton = new JButton("Manchester");
        JButton manchesterDiffButton = new JButton("Manchester Différentiel");
        JButton millerButton = new JButton("Miller");

        //Action listeners pour les buttons
        nrzButton.addActionListener(new CodeButtonListener("NRZ"));
        nrziButton.addActionListener(new CodeButtonListener("NRZI"));
        manchesterButton.addActionListener(new CodeButtonListener("Manchester"));
        manchesterDiffButton.addActionListener(new CodeButtonListener("Manchester Différentiel"));
        millerButton.addActionListener(new CodeButtonListener("Miller"));

        // un Panel pour les buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5));
        buttonPanel.add(nrzButton);
        buttonPanel.add(nrziButton);
        buttonPanel.add(manchesterButton);
        buttonPanel.add(manchesterDiffButton);
        buttonPanel.add(millerButton);

        add(buttonPanel, BorderLayout.NORTH);
        //Cette boucle assure que l'utilisateur entre une chaîne binaire valide avant de poursuivre dans le programme

        while (!isValidInput) {
        	// Invite l'utilisateur à saisir une chaîne binaire
            String userInput = JOptionPane.showInputDialog("Saisir la chaîne binaire :");
            
         // Vérifie si la chaîne saisie est une chaîne binaire valide
            if (isValidBinaryString(userInput)) {
            	// Si la saisie est valide, enregistre la chaîne binaire et met fin à la boucle
                binaryString = userInput;
                isValidInput = true;
            } else {
            	// Si la saisie n'est pas valide, affiche un message d'erreur
                JOptionPane.showMessageDialog(null, "Veuillez saisir une chaîne binaire valide (contenant seulement des 0 et 1).");
            }
        }
     // Affiche une boîte de dialogue pour permettre à l'utilisateur de choisir un code d'encodage
        selectedCode = (String) JOptionPane.showInputDialog(
            null,  // Composant parent (null pour une boîte de dialogue indépendante)
            "Choisir le code :",  // Message de la boîte de dialogue
            "Code Choisi",  // Titre de la boîte de dialogue
            JOptionPane.QUESTION_MESSAGE,  // Type de message (QUESTION_MESSAGE affiche un point d'interrogation)
            null,  // Icône (null pour utiliser l'icône par défaut)
            new String[]{"NRZ", "NRZI", "Manchester", "Manchester Différentiel", "Miller"},  // Options de choix
            "NRZ"  // Option par défaut sélectionnée
        );


       

        // Créer un panneau personnalisé pour dessiner le signal
        SignalPanel signalPanel = new SignalPanel();
        add(signalPanel);

        // Rafraîchir l'affichage
        signalPanel.repaint();
    }

    private class SignalPanel extends JPanel {
    	
    	//méthode pour dessiner les lignes verticaux pointillées
    	public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2){
            Graphics2D g2d = (Graphics2D) g.create();
            Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2d.setStroke(dashed);
            g2d.drawLine(x1, y1, x2, y2);
            g2d.dispose();
    }
    	
    	//méthode pour déssiner les Axes avec des flèches à la fin
    	private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
    	    Graphics2D g2d = (Graphics2D) g;
    	    g2d.drawLine(x1, y1, x2, y2);
    	    double angle = Math.atan2(y2 - y1, x2 - x1);
    	    int arrowSize = 10;
    	    int x3 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
    	    int y3 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
    	    int x4 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
    	    int y4 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));
    	    g2d.drawLine(x2, y2, x3, y3);
    	    g2d.drawLine(x2, y2, x4, y4);
    	}


        @Override
     
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Dessinez le repère
            int nv = 50; // Niveau vertical
            int prevy;
            prevy = 0;
            int pred=-1; //un variable pour indiqué le bit précedent
            drawArrow(g, 0, getHeight() / 2, getWidth(), getHeight() / 2); // Axe horizontal en flèche
            drawArrow(g, 20, getHeight() ,20, 0 );// Axe vertical en flèche

            // Dessinez le signal
            int x = 50;
            int y = getHeight() / 2;
            g.setFont(new Font("Arial", Font.BOLD, 18)); 

            switch (selectedCode) {
                case "NRZ":
                	//pour dessiner les lignes pointillés
                    for (int i = 1; i <= binaryString.length() + 1; i++) {
                        drawDashedLine(g, x * i, y - 100, x * i, y + 100);
                    }

                    for (int i = 0; i < binaryString.length(); i++) {
                        char bit = binaryString.charAt(i);
                        int nextX = x + 50; // Espacement entre les bits
                        g.drawString(String.valueOf(bit), nextX - 25, y - 120 );//afficher la chaine binaire

                        if (bit == '0') {
                            if (prevy != 0) {
                                g.drawLine(x, prevy, x, y + nv);
                            }
                            g.drawLine(x, y + nv, nextX, y + nv);
                            prevy = y + nv;
                        } else {
                            if (prevy != 0) {
                                g.drawLine(x, prevy, x, y - nv);
                            }
                            g.drawLine(x, y - nv, nextX, y - nv);
                            prevy = y - nv;
                        }

                        x = nextX;
                    }
                    break;

                case "NRZI":
                	for (int i = 1; i <= binaryString.length() + 1; i++) {
                        drawDashedLine(g, x * i, y - 100, x * i, y + 100);
                    }

                    for (int i = 0; i < binaryString.length(); i++) {
                        char bit = binaryString.charAt(i);
                        int nextX = x + 50; 
                        g.drawString(String.valueOf(bit), nextX - 25, y - 120 );

                        if (bit == '1') {
                            if (prevy != 0) {
                                g.drawLine(x, prevy, x, y + nv);
                            }
                            g.drawLine(x, y + nv, nextX, y + nv);
                            prevy = y + nv;
                        } else {
                            if (prevy != 0) {
                                g.drawLine(x, prevy, x, y - nv);
                            }
                            g.drawLine(x, y - nv, nextX, y - nv);
                            prevy = y - nv;
                        }

                        x = nextX;
                    }
                    break;

                case "Manchester":
                	
                	for (int i = 1; i <= binaryString.length() + 1; i++) {
                        drawDashedLine(g, x * i, y - 100, x * i, y + 100);
                    }
                	for (int i = 0; i < binaryString.length(); i++) {
                        char bit = binaryString.charAt(i);
                        int nextX = x + 50; // Espacement entre les bits
                        g.drawString(String.valueOf(bit), nextX - 25, y - 120 );
                        
                        if (bit == '1') {
                        	if(pred==1) {
                        		g.drawLine(x, y-nv, x, y + nv);
                        	}
                        	 
                        g.drawLine(x, y-nv,x+25, y-nv);
                        g.drawLine(x+25, y-nv,x+25, y+nv);
                        g.drawLine(x+25, y+nv,nextX, y+nv);
                        prevy = y + nv;
                        pred=1;
                	    }
                        else {
                        	if(pred==0) {
                        		g.drawLine(x, y-nv, x, y + nv);
                        	}
                        	
                       g.drawLine(x, y+nv,x+25, y+nv);
                       g.drawLine(x+25, y+nv,x+25, y-nv);
                       g.drawLine(x+25, y-nv,nextX, y-nv);
                       prevy = y + nv;
                       pred=0;
                        	
                        }
                        x = nextX;
                	}
                	
             
                    break;

                case "Manchester Différentiel":
                	
                	for (int i = 1; i <= binaryString.length() + 1; i++) {
                        drawDashedLine(g, x * i, y - 100, x * i, y + 100);
                    }

                    for (int i = 0; i < binaryString.length(); i++) {
                        char bit = binaryString.charAt(i);
                        int nextX = x + 50; // Espacement entre les bits
                        g.drawString(String.valueOf(bit), nextX - 25, y - 120 );
                     

                        if (bit == '1') {
                        	if(pred==1) {
                        		g.drawLine(x, y-nv, x, y + nv);
                        	}
                            // Transition
                        	if(prevy==y+nv) {
                        		g.drawLine(x, y+nv,x+25, y+nv);
                                g.drawLine(x+25, y+nv,x+25, y-nv);
                                g.drawLine(x+25, y-nv,nextX, y-nv);
                                prevy = y - nv;
                        	}
                        	else {
                        		 
                           		 g.drawLine(x, y-nv,x+25, y-nv);
                                 g.drawLine(x+25, y-nv,x+25, y+nv);
                                 g.drawLine(x+25, y+nv,nextX, y+nv);
                                 prevy = y+nv;	
                        	}
                        	pred=1;
                        }
                        else if (bit =='0') {
                        	if(pred==0) {
                        		g.drawLine(x, y-nv, x, y + nv);
                        	}
                        	
                        	if(prevy==y-nv) {
                        		g.drawLine(x, y-nv,x, y+nv);
                        		g.drawLine(x, y+nv,x+25, y+nv);
                                g.drawLine(x+25, y+nv,x+25, y-nv);
                                g.drawLine(x+25, y-nv,nextX, y-nv);
                                prevy = y - nv;
                        		
                        	}
                        	else {
                        		g.drawLine(x, y-nv,x, y+nv);
                        		g.drawLine(x, y-nv,x+25, y-nv);
                                g.drawLine(x+25, y-nv,x+25, y+nv);
                                g.drawLine(x+25, y+nv,nextX, y+nv);
                                prevy = y+ nv;
                        		
                        	}	
                        	pred=0;
                        }
                        
                        else if (i > 0 && binaryString.charAt(i - 1) == '0') {
                        	if(prevy==y-nv) {
                        		g.drawLine(x, y-nv,x, y+nv);
                        		g.drawLine(x, y+nv,x+25, y+nv);
                                g.drawLine(x+25, y+nv,x+25, y-nv);
                                g.drawLine(x+25, y-nv,nextX, y-nv);
                                prevy = y - nv;
                        		
                        	}
                        	else {
                        		g.drawLine(x, y-nv,x, y+nv);
                        		g.drawLine(x, y-nv,x+25, y-nv);
                                g.drawLine(x+25, y-nv,x+25, y+nv);
                                g.drawLine(x+25, y+nv,nextX, y+nv);
                                prevy = y+ nv;
                        		
                        	}	
                        	
                        }
                        
                        
                        x = nextX;
                    }
                	
                	break;

                case "Miller":
                    //valeur du bit précédent
                    
                    
                	for (int i = 1; i <= binaryString.length() + 1; i++) {
                        drawDashedLine(g, x * i, y - 100, x * i, y + 100);
                        System.out.println();
                    }    
          

                    for (int i = 0; i < binaryString.length(); i++) {
                    	
                        char bit = binaryString.charAt(i);
                        
                        int nextX = x + 50; // Espacement entre les bits
                        g.drawString(String.valueOf(bit), nextX - 25, y - 120 );

                        if (bit == '1') {
                            // Transition
                        	if(prevy==y+nv) {
                        		g.drawLine(x, y+nv,x+25, y+nv);
                                g.drawLine(x+25, y+nv,x+25, y-nv);
                                g.drawLine(x+25, y-nv,nextX, y-nv);
                                prevy = y - nv;
                                
                        	}
                        	else {
                        		 
                           		 g.drawLine(x, y-nv,x+25, y-nv);
                                 g.drawLine(x+25, y-nv,x+25, y+nv);
                                 g.drawLine(x+25, y+nv,nextX, y+nv);
                                 prevy = y+nv;	
                        	}
                        	pred=1;
                        }
                        else if (bit =='0') {
                        	if(i==0) {
                        		if(prevy==y+nv) {
                            		g.drawLine(x, y + nv, nextX, y + nv);
                                    prevy = y + nv;
                            		
                            	}
                            	else {
                            		g.drawLine(x, y - nv, nextX, y - nv);
                                    prevy = y - nv;
                            		
                            	}
                        		
                        		pred=0;
                        	}
                        	
                        	 else {
                        		 
                             	if(pred==0) {
                             
                             	if(prevy==y+nv) {
                             		g.drawLine(x, y+nv,x, y-nv);
                             		g.drawLine(x, y - nv, nextX, y - nv);
                                     prevy = y - nv;
                             		
                             	}
                             	else {
                             		g.drawLine(x, y-nv,x, y+nv);
                             		g.drawLine(x, y + nv, nextX, y + nv);
                                     prevy = y + nv;
                             		
                             	}
                             	pred=0; 
                             	
                             }
                             else {
                             	if(prevy==y-nv) {
                             		
                             		g.drawLine(x, y - nv, nextX, y - nv);
                                     prevy = y - nv;
                             		
                             	}
                             	else {
                             		
                             		g.drawLine(x, y + nv, nextX, y + nv);
                                     prevy = y + nv;
                             		
                             	}
                             	pred=0;
                             }
                             	
                             }
                        }
                       
                        x = nextX;
                    }
                
                	
                	break;

          
            }

            // Rafraîchir l'affichage
            repaint();
        }

    }
    private class CodeButtonListener implements ActionListener {
        private String code;

        public CodeButtonListener(String code) {
            this.code = code;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedCode = code;

            // Trigger a repaint to update the display based on the selected code
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignalTraceurSwing traceur = new SignalTraceurSwing();
            traceur.setVisible(true);
        });
    }
}
