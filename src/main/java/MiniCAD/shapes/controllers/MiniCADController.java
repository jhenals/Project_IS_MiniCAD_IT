package MiniCAD.shapes.controllers;

import MiniCAD.command.MiniCadCommandHandler;
import MiniCAD.exceptions.ParseException;
import MiniCAD.shapes.interpreter.Context;
import MiniCAD.shapes.interpreter.commands.CommandExprIF;
import MiniCAD.shapes.interpreter.commands.UndoableCmdExprIF;
import MiniCAD.shapes.interpreter.lexerparser.CommandParser;
import MiniCAD.util.NumericDocumentFilter;
import ObserverCommandFlyweight.is.shapes.model.GraphicObject;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serial;


public class MiniCADController extends JPanel {

    @Serial
    private static final long serialVersionUID = -1200564294398210114L;
    private final MiniCadCommandHandler cmdHandler;
    static final int offset = 10;
    static final double zoom_factor= 0.1;
    private String objId= null;
    private GraphicObject subject;
    private CommandParser commandParser;
    private Context context;
    private JTextArea propertiesArea;


    public void setControlledObject(GraphicObject go, String id) {
        subject = go;
        objId = id;
        propertiesArea.setText(id);
    }

    public MiniCADController(MiniCadCommandHandler cmdH, Context context) {
        this(null,cmdH,context);
    }

    public MiniCADController(GraphicObject go, MiniCadCommandHandler cmdH, Context c){
        cmdHandler = cmdH;
        subject = go;
        context = c;
        commandParser = new CommandParser();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel commandPanel = new JPanel();
        commandPanel.setPreferredSize(new Dimension(300, 400));
        commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));

        commandPanel.add(zoomMovePanel());
        commandPanel.add(moveOffPanel());
        commandPanel.add(commandButtonsPanel());
        commandPanel.add(operazioniAvanzatePanel());

        JPanel propViewerPanel = new JPanel();
        propViewerPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
        propertiesArea = propertiesViewer();
        propViewerPanel.add(new JScrollPane(propertiesArea));

        add(commandPanel);
        add(propViewerPanel);
    }

     private JPanel zoomMovePanel(){
        JPanel zoomMovePanel = new JPanel();
        JPanel grid = new JPanel(new GridLayout(3, 3));
        JPanel zoom = new JPanel(new GridLayout(1, 2));

        JButton minus = new JButton("-");

        minus.addActionListener(e -> {
            clearPropertiesViewer();
            if (subject != null) {
                String scaleMinusInput = String.format("scale %s %s", objId, 1.0 - zoom_factor);
                try {
                    UndoableCmdExprIF scaleMinus = (UndoableCmdExprIF) commandParser.parseCommand(scaleMinusInput);
                    cmdHandler.handle(scaleMinus);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        zoom.add(minus);

        JButton plus = new JButton("+");
        plus.addActionListener(e -> {
            clearPropertiesViewer();
            if (subject != null) {
                String scalePlusInput = String.format("scale %s %s", objId, 1.0 + zoom_factor);
                try {
                    UndoableCmdExprIF scalePlus = (UndoableCmdExprIF) commandParser.parseCommand(scalePlusInput);
                    cmdHandler.handle(scalePlus);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        zoom.add(plus);
        zoom.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton nw = new JButton("\\");

        nw.addActionListener(e -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveNWInput = "mv "+ objId + "("+ (p.getX()  - offset) +","+(p.getY() - offset)+")";
                try {
                    UndoableCmdExprIF mvNW = (UndoableCmdExprIF) commandParser.parseCommand(moveNWInput);
                    cmdHandler.handle(mvNW);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        grid.add(nw);

        JButton n = new JButton("|");
        n.addActionListener(e -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveNInput = "mv "+ objId + "("+ p.getX() +","+(p.getY() - offset)+")";
                try {
                    UndoableCmdExprIF mvN = (UndoableCmdExprIF) commandParser.parseCommand(moveNInput);
                    cmdHandler.handle(mvN);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        grid.add(n);

        JButton ne = new JButton("/");
        ne.addActionListener(e -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveNEInput = "mv "+ objId + "("+ (p.getX()  + offset) +","+(p.getY() - offset)+")";
                try {
                    UndoableCmdExprIF mvNE = (UndoableCmdExprIF) commandParser.parseCommand(moveNEInput);
                    cmdHandler.handle(mvNE);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //cmdHandler.handle(new MoveCommand(subject, new Point2D.Double(p.getX() + offset, p.getY() - offset)));

        });

        grid.add(ne);

        JButton w = new JButton("<-");
        w.addActionListener(e -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveWInput = "mv "+ objId + "("+ (p.getX()  - offset) +","+p.getY()+")";
                try {
                    UndoableCmdExprIF mvW = (UndoableCmdExprIF) commandParser.parseCommand(moveWInput);
                    cmdHandler.handle(mvW);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //cmdHandler.handle(new MoveCommand(subject, new Point2D.Double(p.getX() - offset, p.getY())));
        });

        grid.add(w);

        grid.add(zoom);

        JButton e = new JButton("->");
        e.addActionListener(e1 -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveEInput = "mv "+ objId + "("+ (p.getX()  + offset) +","+p.getY()+")";
                try {
                    UndoableCmdExprIF mvE = (UndoableCmdExprIF) commandParser.parseCommand(moveEInput);
                    cmdHandler.handle(mvE);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //cmdHandler.handle(new MoveCommand(subject, new Point2D.Double(p.getX() + offset, p.getY())));
        });

        grid.add(e);

        JButton sw = new JButton("/");

        sw.addActionListener(e12 -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveSWInput = "mv "+ objId + "("+ (p.getX()  - offset) +","+(p.getY() + offset)+")";
                try {
                    UndoableCmdExprIF mvSW = (UndoableCmdExprIF) commandParser.parseCommand(moveSWInput);
                    cmdHandler.handle(mvSW);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //cmdHandler.handle(new MoveCommand(subject, new Point2D.Double(p.getX() - offset, p.getY() + offset)));
        });
        grid.add(sw);

        JButton s = new JButton("|");
        s.addActionListener(e13 -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveSInput = "mv "+ objId + "("+ p.getX() +","+(p.getY() + offset)+")";
                try {
                    UndoableCmdExprIF mvS = (UndoableCmdExprIF) commandParser.parseCommand(moveSInput);
                    cmdHandler.handle(mvS);
                }catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //cmdHandler.handle(new MoveCommand(subject, new Point2D.Double(p.getX(), p.getY() + offset)));
        });

        grid.add(s);

        JButton se = new JButton("\\");
        se.addActionListener(e14 -> {
            clearPropertiesViewer();
            if (subject == null) {
                return;
            }
            else if (subject != null) {
                Point2D p = subject.getPosition();
                String moveSEInput = "mv "+ objId + "("+ (p.getX()  + offset) +","+(p.getY() + offset)+")";
                try {
                    UndoableCmdExprIF mvSE = (UndoableCmdExprIF) commandParser.parseCommand(moveSEInput);
                    cmdHandler.handle(mvSE);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //cmdHandler.handle(new MoveCommand(subject, new Point2D.Double(p.getX() + offset, p.getY() + offset)));
        });
        grid.add(se);
        zoomMovePanel.add(grid);
        return zoomMovePanel;
    }


    private JPanel moveOffPanel() { //TODO
        JTextField  newXAxisField = new JTextField();
        newXAxisField.setPreferredSize(new Dimension(50, 20));
        setNumericFilter(newXAxisField);

        JTextField  newYAxisField = new JTextField();
        newYAxisField.setPreferredSize(new Dimension(50, 20));
        setNumericFilter(newYAxisField);

        JButton sposta = new JButton("Move");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("X-Axis:"));
        inputPanel.add(newXAxisField);
        inputPanel.add(new JLabel("Y-Axis:"));
        inputPanel.add(newYAxisField);
        inputPanel.add(sposta);

        return inputPanel;
    }

    private static void setNumericFilter(JTextField textField){
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
    }

    private JPanel commandButtonsPanel() {
        JPanel cmdButtonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c= new GridBagConstraints();

        //Buttons
        JButton deleteButton = new JButton("Delete");
        JButton areaButton = new JButton("Area");
        JButton perimButton = new JButton("Perimeter");
        JButton viewProperty = new JButton("View Property");

        JPanel groupPanel = manageGroupFeaturePanel();

        deleteButton.addActionListener(e -> {
            try{
                updatePropertiesViewer("del", objId);
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        areaButton.addActionListener(e -> {
            try{
                updatePropertiesViewer("area", objId);
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        perimButton.addActionListener(e -> {
            try{
                updatePropertiesViewer("perimeter" , objId);
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        viewProperty.addActionListener(e -> {
            try {
                updatePropertiesViewer("ls", objId);
            } catch (ParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        //constraints
        c.gridx=0;
        c.gridy=0;
        c.gridwidth= 2;
        c.fill= GridBagConstraints.HORIZONTAL;
        cmdButtonsPanel.add(deleteButton, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        cmdButtonsPanel.add(areaButton, c);

        c.gridx = 1;
        c.gridy = 1;
        cmdButtonsPanel.add(perimButton, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth= 2;
        c.fill= GridBagConstraints.HORIZONTAL;
        cmdButtonsPanel.add(viewProperty, c);

        c.gridx = 0;
        c.gridy = 3;
        //c.gridwidth= 3;
        c.fill= GridBagConstraints.HORIZONTAL;
        cmdButtonsPanel.add(groupPanel, c);

        return cmdButtonsPanel;
    }

    private JPanel manageGroupFeaturePanel() {
        JPanel panel = new JPanel((new GridBagLayout()));
        GridBagConstraints c= new GridBagConstraints();

        JTextField  idsField = new JTextField("Insert ids to group or group id to ungroup");
        idsField.setForeground(Color.GRAY);

        JButton groupButton = new JButton("Group");
        JButton ungroupButton = new JButton("Ungroup");

        groupButton.addActionListener( e -> {
            clearPropertiesViewer();
            String input = idsField.getText().trim();
            if (!input.isEmpty()) {
                String[] ids = input.split("\\s+");
                if (ids.length > 1) {
                    handleGroupAction(ids);
                    idsField.setText("");
                } else {
                    showMessage("Please enter multiple IDs separated by spaces for grouping.");
                }
            } else {
                showMessage("Please enter IDs to group.");
            }
        });

        ungroupButton.addActionListener( e -> {
            clearPropertiesViewer();
            String input = idsField.getText().trim();
            if (!input.isEmpty()) {
                String[] ids = input.split("\\s+");
                if (ids.length == 1) {
                    handleUngroupAction(ids[0]);
                    idsField.setText("");
                } else {
                    showMessage("Please enter a single group ID to ungroup.");
                }
            } else {
                showMessage("Please enter a group ID to ungroup.");
            }
        });
        c.fill= GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(idsField, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panel.add(groupButton,c);

        c.gridx = 1;
        c.gridy = 1;
        panel.add(ungroupButton,c);
        return panel;
    }

    //TODO
    private void handleUngroupAction(String gid) {
        String ungroupInput = "ungrp "+ gid;
        try {
            UndoableCmdExprIF ungroupCommand = (UndoableCmdExprIF) commandParser.parseCommand(ungroupInput);
            cmdHandler.handle(ungroupCommand);
            showMessage(gid + " is ungrouped.");
        } catch (ParseException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    //TODO
    private void handleGroupAction(String[] ids) {
        String idList = transformInput(ids);
        String groupInput = "grp "+ idList;
        System.out.println(groupInput);
        try {
            UndoableCmdExprIF groupCommand = (UndoableCmdExprIF) commandParser.parseCommand(groupInput);
            cmdHandler.handle(groupCommand);
            showMessage("New group element is created.");
        } catch (ParseException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String transformInput(String[] ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            sb.append(ids[i]);
            if (i < ids.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    private JPanel operazioniAvanzatePanel() {
        JPanel advOpsPanel = new JPanel();
        advOpsPanel.setPreferredSize(new Dimension(280,115));
        advOpsPanel.setLayout(new BoxLayout(advOpsPanel, BoxLayout.PAGE_AXIS));

        advOpsPanel.setBorder(BorderFactory.createTitledBorder("Advance Operations"));

        //Visualizza Proprietà
        JMenuBar advViewPropMenuBar = new JMenuBar();
        JMenu advancePropertyViewMenu = new JMenu("Advance View");
        JMenuItem imgItem = new JMenuItem("View all images");
        JMenuItem rectItem = new JMenuItem("View all rectangles");
        JMenuItem circleItem = new JMenuItem("View all circles");
        JMenuItem groupItem = new JMenuItem("View all groups");
        JMenuItem allItem = new JMenuItem("View all");

        imgItem.addActionListener(e -> {
            try {
                updatePropertiesViewer("ls", "img");
            } catch (ParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        rectItem.addActionListener(e -> {
            try {
                updatePropertiesViewer("ls", "rectangle");
            } catch (ParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        circleItem.addActionListener(e -> {
            try {
                updatePropertiesViewer("ls", "circle");
            } catch (ParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        groupItem.addActionListener(e -> {
            try {
                updatePropertiesViewer("ls", "groups");
            } catch (ParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        allItem.addActionListener(e -> {
            try {
                updatePropertiesViewer("ls", "all");
            } catch (ParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        advancePropertyViewMenu.add(imgItem);
        advancePropertyViewMenu.add(rectItem);
        advancePropertyViewMenu.add(circleItem);
        advancePropertyViewMenu.add(groupItem);
        advancePropertyViewMenu.add(allItem);

        //Area
        JMenuBar areaMenuBar = new JMenuBar();
        JMenu calculateAreasMenu = new JMenu("Calculate Areas");
        JMenuItem areaRectItem = new JMenuItem("Areas of all rectangles");
        JMenuItem areaCircleItem = new JMenuItem("Areas of all circles");
        JMenuItem areaAllItem = new JMenuItem("Total Areas");

        areaRectItem.addActionListener(e -> {
            try{
                updatePropertiesViewer("area" , "rectangle");
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        areaCircleItem.addActionListener(e -> {
            try{
                updatePropertiesViewer("area" , "circle");
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        areaAllItem.addActionListener(e -> {
            try{
                updatePropertiesViewer("area" , "all");
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        calculateAreasMenu.add(areaRectItem);
        calculateAreasMenu.add(areaCircleItem);
        calculateAreasMenu.add(areaAllItem);

        //Perimetro
        JMenuBar perimMenuBar = new JMenuBar();
        JMenu calculatePerimsMenu = new JMenu("Calculate Perimeters");
        JMenuItem perimRectItem = new JMenuItem("Perimeters of all rectangles");
        JMenuItem perimCircleItem = new JMenuItem("Perimeters of all circles");
        JMenuItem perimAllItem = new JMenuItem("Total Perimeters");

        perimRectItem.addActionListener(e -> {
            try{
                updatePropertiesViewer("perimeter" , "rectangle");
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        perimCircleItem.addActionListener(e -> {
            try{
                updatePropertiesViewer("perimeter" , "circle");
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        perimAllItem.addActionListener(e -> {
            try{
                updatePropertiesViewer("perimeter" , "all");
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        });


        calculatePerimsMenu.add(perimRectItem);
        calculatePerimsMenu.add(perimCircleItem);
        calculatePerimsMenu.add(perimAllItem);

        advViewPropMenuBar.add(advancePropertyViewMenu);
        areaMenuBar.add(calculateAreasMenu);
        perimMenuBar.add(calculatePerimsMenu);

        advOpsPanel.add(areaMenuBar);
        advOpsPanel.add(perimMenuBar);
        advOpsPanel.add(advViewPropMenuBar);

        return advOpsPanel;
    }


    private void updatePropertiesViewer(String cmd, String param) throws ParseException, IOException {
        if(subject != null ){
            propertiesArea.setText(getPropertiesAsString(cmd, param));
        }
    }

    private void showMessage(String msg)  {
        propertiesArea.setText(msg);
    }

    private String getPropertiesAsString(String cmd, String param) {
        String commandInput = cmd + " " + param;

        try {
            CommandExprIF command = commandParser.parseCommand(commandInput);
            return command.interpreta(context).toString();
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private JTextArea propertiesViewer() {
        propertiesArea = new JTextArea();
        propertiesArea.setBackground(Color.WHITE);
        propertiesArea.setPreferredSize(new Dimension(300, 200));
        propertiesArea.setEditable(false);
        return propertiesArea;
    }


    private void clearPropertiesViewer() {
        propertiesArea.setText("");
    }


}
