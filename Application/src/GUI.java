/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvsomecode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.shape.Line;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author daniel
 */
public class GUI extends javax.swing.JFrame {

    RiscvAnalyzer rvanalysis;
    Simulation sim;
    int reg_base_selection_value = 10;
    int mem_base_selection_value = 10;
    boolean updateTXTfield;
    int lastLineCount;
    String currentPath, memLeftLimit, memRightLimit;
    int base_selection_filter;
    int RIGHT_MEM_LIMIT = 1048576;
    String DEFAULT_MIN = "0";
    String DEFAULT_MAX = "100";
    String ERROR_MSG;
    int leftmemLimit;
    int rightmemLimit;
    DefaultTableModel dtmMemory = new DefaultTableModel();
    ArrayList<GuiMemoryWord> memoryList = new ArrayList<>();
    int EXEC_ALL = 1;
    int EXEC_STEP = 2;
    int EXEC_STOP = 3;
    int EXEC_MODE = 0;//1 all, 2 step
    int EXEC_STEP_STARTED = 0;
    boolean end_flag = false;
    int line_simulation_counter = 0;

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        //memoryList.

        setTextArea();
        leftmemLimit = 0;
        rightmemLimit = 100;
        updateTXTfield = true;
        lastLineCount = txt_area.getLineCount();
        this.setLocationRelativeTo(null);
        currentPath = "none";
        rvanalysis = new RiscvAnalyzer();
        sim = new Simulation();
        setTableModel();
        updateRegistersView();
        initWindowtConf();

    }

    private void initWindowtConf() {
        ERROR_MSG = "";
        jLabelError.setText(ERROR_MSG);
        base_selection_filter = 0;
        this.memLeftLimit = DEFAULT_MIN;
        this.memRightLimit = DEFAULT_MAX;
        jTxtLeftMemLimit.setText(memLeftLimit);
        jTxtRightMemLimit.setText(memRightLimit);
        jLabe_simulation_end_txt.setVisible(false);
        jlabel_simulation_end_icon.setVisible(false);
        infoMsgLbl();

    }

    private void infoMsgLbl() {
        if (jComboBoxMemoryFilter.getSelectedIndex() == 0) {
            infoLbl.setText("* Maximum address value is 1048576 ");
            addlbl1.setText("");
            addlbl2.setText("");
        } else {
            infoLbl.setText("* Maximum address value is 0x100 000 ");
            addlbl1.setText("0x");
            addlbl2.setText("0x");
        }
    }

    private void setTableModel() {
        String[] cabecera = {"Word Address", "Value"};
        dtmMemory.setColumnIdentifiers(cabecera);
        jTableMemory.setModel(dtmMemory);
    }

    private void fillMemoryList() {
        String address = "";
        String value = "";
        String result = "";
        for (int i = leftmemLimit; i <= rightmemLimit; i += 4) {
            result = sim.mem.getFromMemory(i, mem_base_selection_value);
            if (!result.equals("0")) {
                address = "0x" + Integer.toHexString(i);
                GuiMemoryWord memRecord = new GuiMemoryWord(address, result);
                memoryList.add(memRecord);
            }

        }
    }

    private void setTableData() {
        Object[] datos = new Object[dtmMemory.getColumnCount()];
        int i = 1;
        String base_str = "";
        switch (mem_base_selection_value) {
            case 16:
                base_str = "0x";
                break;
            case 2:
                base_str = "0b";
                break;
        }
        dtmMemory.setRowCount(0);
        for (GuiMemoryWord mem : memoryList) {
            datos[0] = mem.getAddress();
            datos[1] = base_str + mem.getValue();
            i++;
            dtmMemory.addRow(datos);
        }
        updateTable();
    }

    private void updateTable() {
        jTableMemory.setModel(dtmMemory);
    }

    @Override
    public Image getIconImage() {
        Image retVal = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("images/icon1.png"));
        return retVal;
    }

    private void makeAnalysis() {

        saveTxtArea(currentPath);
        try {
            rvanalysis.makeAnalysis(currentPath);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateErrMsg();
    }

    private void saveTxtArea(String path) {
        BufferedWriter writer = null;
        try {
            // TODO add your handling code here:
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(this.txt_area.getText());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setTextArea() {
        LineNumberTxtArea lineNumberingTextArea = new LineNumberTxtArea(txt_area);
        scrollPaneTxt.setRowHeaderView(lineNumberingTextArea);
        txt_area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                lineNumberingTextArea.updateLineNumbers(updateTXTfield);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {

                lineNumberingTextArea.updateLineNumbers(updateTXTfield);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                lineNumberingTextArea.updateLineNumbers(updateTXTfield);
            }
        });
    }

    private void updateRegistersView() {
        x0_val.setText(sim.reg.readRegisterValue("x0", reg_base_selection_value));
        x1_val.setText(sim.reg.readRegisterValue("x1", reg_base_selection_value));
        x2_val.setText(sim.reg.readRegisterValue("x2", reg_base_selection_value));
        x3_val.setText(sim.reg.readRegisterValue("x3", reg_base_selection_value));
        x4_val.setText(sim.reg.readRegisterValue("x4", reg_base_selection_value));
        x5_val.setText(sim.reg.readRegisterValue("x5", reg_base_selection_value));
        x6_val.setText(sim.reg.readRegisterValue("x6", reg_base_selection_value));
        x7_val.setText(sim.reg.readRegisterValue("x7", reg_base_selection_value));
        x8_val.setText(sim.reg.readRegisterValue("x8", reg_base_selection_value));
        x9_val.setText(sim.reg.readRegisterValue("x9", reg_base_selection_value));
        x10_val.setText(sim.reg.readRegisterValue("x10", reg_base_selection_value));
        x11_val.setText(sim.reg.readRegisterValue("x11", reg_base_selection_value));
        x12_val.setText(sim.reg.readRegisterValue("x12", reg_base_selection_value));
        x13_val.setText(sim.reg.readRegisterValue("x13", reg_base_selection_value));
        x14_val.setText(sim.reg.readRegisterValue("x14", reg_base_selection_value));
        x15_val.setText(sim.reg.readRegisterValue("x15", reg_base_selection_value));
        x16_val.setText(sim.reg.readRegisterValue("x16", reg_base_selection_value));
        x17_val.setText(sim.reg.readRegisterValue("x17", reg_base_selection_value));
        x18_val.setText(sim.reg.readRegisterValue("x18", reg_base_selection_value));
        x19_val.setText(sim.reg.readRegisterValue("x19", reg_base_selection_value));
        x20_val.setText(sim.reg.readRegisterValue("x20", reg_base_selection_value));
        x21_val.setText(sim.reg.readRegisterValue("x21", reg_base_selection_value));
        x22_val.setText(sim.reg.readRegisterValue("x22", reg_base_selection_value));
        x23_val.setText(sim.reg.readRegisterValue("x23", reg_base_selection_value));
        x24_val.setText(sim.reg.readRegisterValue("x24", reg_base_selection_value));
        x25_val.setText(sim.reg.readRegisterValue("x25", reg_base_selection_value));
        x26_val.setText(sim.reg.readRegisterValue("x26", reg_base_selection_value));
        x27_val.setText(sim.reg.readRegisterValue("x27", reg_base_selection_value));
        x28_val.setText(sim.reg.readRegisterValue("x28", reg_base_selection_value));
        x29_val.setText(sim.reg.readRegisterValue("x29", reg_base_selection_value));
        x30_val.setText(sim.reg.readRegisterValue("x30", reg_base_selection_value));
        x31_val.setText(sim.reg.readRegisterValue("x31", reg_base_selection_value));
    }

    private void updateErrMsg() {
        error_log_area.setText("");
        try (BufferedReader br = new BufferedReader(new FileReader("ERRORS.log"))) {
            String line_first;
            int i = 0;
            while ((line_first = br.readLine()) != null) {
                error_log_area.append(line_first);
                error_log_area.append("\n");

            }
        } catch (IOException ex) {
            Logger.getLogger(RiscV_Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        x0_val1 = new javax.swing.JTextField();
        x1_val1 = new javax.swing.JTextField();
        x3_val1 = new javax.swing.JTextField();
        x2_val1 = new javax.swing.JTextField();
        x4_val1 = new javax.swing.JTextField();
        x5_val1 = new javax.swing.JTextField();
        x6_val1 = new javax.swing.JTextField();
        x7_val1 = new javax.swing.JTextField();
        x8_val1 = new javax.swing.JTextField();
        x9_val1 = new javax.swing.JTextField();
        x10_val1 = new javax.swing.JTextField();
        x11_val1 = new javax.swing.JTextField();
        x12_val1 = new javax.swing.JTextField();
        x13_val1 = new javax.swing.JTextField();
        x14_val1 = new javax.swing.JTextField();
        x15_val1 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        next_step_button = new javax.swing.JButton();
        restart_button = new javax.swing.JButton();
        scrollPaneTxt = new javax.swing.JScrollPane();
        txt_area = new javax.swing.JTextArea();
        checkSintaxButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        execute_all_button = new javax.swing.JButton();
        step_back_button = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        error_log_area = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        x16_val = new javax.swing.JTextField();
        x17_val = new javax.swing.JTextField();
        x19_val = new javax.swing.JTextField();
        x18_val = new javax.swing.JTextField();
        x20_val = new javax.swing.JTextField();
        x21_val = new javax.swing.JTextField();
        x22_val = new javax.swing.JTextField();
        x23_val = new javax.swing.JTextField();
        x24_val = new javax.swing.JTextField();
        x25_val = new javax.swing.JTextField();
        x26_val = new javax.swing.JTextField();
        x27_val = new javax.swing.JTextField();
        x28_val = new javax.swing.JTextField();
        x29_val = new javax.swing.JTextField();
        x30_val = new javax.swing.JTextField();
        x31_val = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableMemory = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jComboBoxMemoryFilter = new javax.swing.JComboBox<>();
        addlbl1 = new javax.swing.JLabel();
        jButtonApplyFilterSettings = new javax.swing.JButton();
        jTxtRightMemLimit = new javax.swing.JTextField();
        addlbl2 = new javax.swing.JLabel();
        jTxtLeftMemLimit = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        infoLbl = new javax.swing.JLabel();
        jLabelError = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jComboBoxMemoryBase = new javax.swing.JComboBox<>();
        reg_base_selection = new javax.swing.JComboBox<>();
        jLabel53 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        x0_val = new javax.swing.JTextField();
        x1_val = new javax.swing.JTextField();
        x3_val = new javax.swing.JTextField();
        x2_val = new javax.swing.JTextField();
        x4_val = new javax.swing.JTextField();
        x5_val = new javax.swing.JTextField();
        x6_val = new javax.swing.JTextField();
        x7_val = new javax.swing.JTextField();
        x8_val = new javax.swing.JTextField();
        x9_val = new javax.swing.JTextField();
        x10_val = new javax.swing.JTextField();
        x11_val = new javax.swing.JTextField();
        x12_val = new javax.swing.JTextField();
        x13_val = new javax.swing.JTextField();
        x14_val = new javax.swing.JTextField();
        x15_val = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jlabel_simulation_end_icon = new javax.swing.JLabel();
        jLabe_simulation_end_txt = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabelLineCounter = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuIOpen = new javax.swing.JMenuItem();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuSaveAs = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        jPanel2.setAlignmentX(0.4F);
        jPanel2.setAlignmentY(0.4F);
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        x0_val1.setText("jTextField1");
        x0_val1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                x0_val1ActionPerformed(evt);
            }
        });
        jPanel2.add(x0_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 12, 238, -1));

        x1_val1.setText("jTextField1");
        jPanel2.add(x1_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 43, 238, -1));

        x3_val1.setText("jTextField1");
        jPanel2.add(x3_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 105, 238, -1));

        x2_val1.setText("jTextField1");
        jPanel2.add(x2_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 74, 238, -1));

        x4_val1.setText("jTextField1");
        jPanel2.add(x4_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 136, 238, -1));

        x5_val1.setText("jTextField1");
        jPanel2.add(x5_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 167, 238, -1));

        x6_val1.setText("jTextField1");
        jPanel2.add(x6_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 198, 238, -1));

        x7_val1.setText("jTextField1");
        jPanel2.add(x7_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 229, 238, -1));

        x8_val1.setText("jTextField1");
        jPanel2.add(x8_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 260, 238, -1));

        x9_val1.setText("jTextField1");
        jPanel2.add(x9_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 291, 238, -1));

        x10_val1.setText("jTextField1");
        jPanel2.add(x10_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 322, 238, -1));

        x11_val1.setText("jTextField1");
        jPanel2.add(x11_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 353, 238, -1));

        x12_val1.setText("jTextField1");
        jPanel2.add(x12_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 384, 238, -1));

        x13_val1.setText("jTextField1");
        jPanel2.add(x13_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 415, 238, -1));

        x14_val1.setText("jTextField1");
        jPanel2.add(x14_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 446, 238, -1));

        x15_val1.setText("jTextField1");
        jPanel2.add(x15_val1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 477, 238, -1));

        jLabel18.setBackground(new java.awt.Color(197, 188, 209));
        jLabel18.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel18.setText("x0");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 12, -1, -1));

        jLabel19.setBackground(new java.awt.Color(197, 188, 209));
        jLabel19.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel19.setText("x2");
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 74, -1, -1));

        jLabel20.setBackground(new java.awt.Color(197, 188, 209));
        jLabel20.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel20.setText("x1");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 43, -1, -1));

        jLabel21.setBackground(new java.awt.Color(197, 188, 209));
        jLabel21.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel21.setText("x3");
        jPanel2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 105, -1, -1));

        jLabel22.setBackground(new java.awt.Color(197, 188, 209));
        jLabel22.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel22.setText("x4");
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 136, -1, -1));

        jLabel23.setBackground(new java.awt.Color(197, 188, 209));
        jLabel23.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel23.setText("x5");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 167, -1, -1));

        jLabel24.setBackground(new java.awt.Color(197, 188, 209));
        jLabel24.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel24.setText("x6");
        jPanel2.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 198, -1, -1));

        jLabel25.setBackground(new java.awt.Color(197, 188, 209));
        jLabel25.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel25.setText("x7");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 229, -1, -1));

        jLabel26.setBackground(new java.awt.Color(197, 188, 209));
        jLabel26.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel26.setText("x8");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, -1, -1));

        jLabel27.setBackground(new java.awt.Color(197, 188, 209));
        jLabel27.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel27.setText("x9");
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 291, -1, -1));

        jLabel28.setBackground(new java.awt.Color(197, 188, 209));
        jLabel28.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel28.setText("x10");
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 322, -1, -1));

        jLabel29.setBackground(new java.awt.Color(197, 188, 209));
        jLabel29.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel29.setText("x11");
        jPanel2.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 353, -1, -1));

        jLabel30.setBackground(new java.awt.Color(197, 188, 209));
        jLabel30.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel30.setText("x12");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 384, -1, -1));

        jLabel31.setBackground(new java.awt.Color(197, 188, 209));
        jLabel31.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel31.setText("x13");
        jPanel2.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 415, -1, -1));

        jLabel32.setBackground(new java.awt.Color(197, 188, 209));
        jLabel32.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel32.setText("x14");
        jPanel2.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 446, -1, -1));

        jLabel33.setBackground(new java.awt.Color(197, 188, 209));
        jLabel33.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel33.setText("x15");
        jPanel2.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 477, -1, -1));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bg.jpg"))); // NOI18N
        jLabel34.setText("jLabel17");
        jPanel2.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 300, 540));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VisualRV 0.1");
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(75, 134, 135));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        next_step_button.setText("Next Step");
        next_step_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next_step_buttonActionPerformed(evt);
            }
        });
        getContentPane().add(next_step_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 10, 160, 20));

        restart_button.setText("Restart");
        restart_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restart_buttonActionPerformed(evt);
            }
        });
        getContentPane().add(restart_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 10, 80, 20));

        txt_area.setBackground(java.awt.Color.lightGray);
        txt_area.setColumns(20);
        txt_area.setRows(5);
        txt_area.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_areaKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_areaKeyPressed(evt);
            }
        });
        scrollPaneTxt.setViewportView(txt_area);

        getContentPane().add(scrollPaneTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 410, 434));

        checkSintaxButton.setText("CheckSintax");
        checkSintaxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkSintaxButtonActionPerformed(evt);
            }
        });
        getContentPane().add(checkSintaxButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 160, 20));

        jButton2.setText("ASMtoBinary");
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 180, 20));

        execute_all_button.setText("Execute All");
        execute_all_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                execute_all_buttonMouseClicked(evt);
            }
        });
        execute_all_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                execute_all_buttonActionPerformed(evt);
            }
        });
        getContentPane().add(execute_all_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 140, 20));

        step_back_button.setText("Step Back");
        getContentPane().add(step_back_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 140, 20));

        error_log_area.setColumns(20);
        error_log_area.setForeground(new java.awt.Color(255, 0, 0));
        error_log_area.setRows(5);
        jScrollPane1.setViewportView(error_log_area);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 520, 410, 100));

        jPanel1.setAlignmentX(0.4F);
        jPanel1.setAlignmentY(0.4F);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        x16_val.setBackground(new java.awt.Color(170, 170, 170));
        x16_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x16_val.setForeground(new java.awt.Color(70, 70, 70));
        x16_val.setText("jTextField1");
        x16_val.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                x16_valActionPerformed(evt);
            }
        });
        jPanel1.add(x16_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 12, 238, -1));

        x17_val.setBackground(new java.awt.Color(128, 185, 236));
        x17_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x17_val.setForeground(new java.awt.Color(70, 70, 70));
        x17_val.setText("jTextField1");
        jPanel1.add(x17_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 43, 238, -1));

        x19_val.setBackground(new java.awt.Color(128, 185, 236));
        x19_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x19_val.setForeground(new java.awt.Color(70, 70, 70));
        x19_val.setText("jTextField1");
        jPanel1.add(x19_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 105, 238, -1));

        x18_val.setBackground(new java.awt.Color(170, 170, 170));
        x18_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x18_val.setForeground(new java.awt.Color(70, 70, 70));
        x18_val.setText("jTextField1");
        jPanel1.add(x18_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 74, 238, -1));

        x20_val.setBackground(new java.awt.Color(170, 170, 170));
        x20_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x20_val.setForeground(new java.awt.Color(70, 70, 70));
        x20_val.setText("jTextField1");
        jPanel1.add(x20_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 136, 238, -1));

        x21_val.setBackground(new java.awt.Color(128, 185, 236));
        x21_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x21_val.setForeground(new java.awt.Color(70, 70, 70));
        x21_val.setText("jTextField1");
        jPanel1.add(x21_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 167, 238, -1));

        x22_val.setBackground(new java.awt.Color(170, 170, 170));
        x22_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x22_val.setForeground(new java.awt.Color(70, 70, 70));
        x22_val.setText("jTextField1");
        jPanel1.add(x22_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 198, 238, -1));

        x23_val.setBackground(new java.awt.Color(128, 185, 236));
        x23_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x23_val.setForeground(new java.awt.Color(70, 70, 70));
        x23_val.setText("jTextField1");
        jPanel1.add(x23_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 229, 238, -1));

        x24_val.setBackground(new java.awt.Color(170, 170, 170));
        x24_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x24_val.setForeground(new java.awt.Color(70, 70, 70));
        x24_val.setText("jTextField1");
        jPanel1.add(x24_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 260, 238, -1));

        x25_val.setBackground(new java.awt.Color(128, 185, 236));
        x25_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x25_val.setForeground(new java.awt.Color(70, 70, 70));
        x25_val.setText("jTextField1");
        jPanel1.add(x25_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 291, 238, -1));

        x26_val.setBackground(new java.awt.Color(170, 170, 170));
        x26_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x26_val.setForeground(new java.awt.Color(70, 70, 70));
        x26_val.setText("jTextField1");
        jPanel1.add(x26_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 322, 238, -1));

        x27_val.setBackground(new java.awt.Color(128, 185, 236));
        x27_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x27_val.setForeground(new java.awt.Color(70, 70, 70));
        x27_val.setText("jTextField1");
        jPanel1.add(x27_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 353, 238, -1));

        x28_val.setBackground(new java.awt.Color(170, 170, 170));
        x28_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x28_val.setForeground(new java.awt.Color(70, 70, 70));
        x28_val.setText("jTextField1");
        jPanel1.add(x28_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 384, 238, -1));

        x29_val.setBackground(new java.awt.Color(128, 185, 236));
        x29_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x29_val.setForeground(new java.awt.Color(70, 70, 70));
        x29_val.setText("jTextField1");
        jPanel1.add(x29_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 415, 238, -1));

        x30_val.setBackground(new java.awt.Color(170, 170, 170));
        x30_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x30_val.setForeground(new java.awt.Color(70, 70, 70));
        x30_val.setText("jTextField1");
        jPanel1.add(x30_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 446, 238, -1));

        x31_val.setBackground(new java.awt.Color(128, 185, 236));
        x31_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x31_val.setForeground(new java.awt.Color(70, 70, 70));
        x31_val.setText("jTextField1");
        jPanel1.add(x31_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 477, 238, -1));

        jLabel1.setBackground(new java.awt.Color(197, 188, 209));
        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(254, 254, 254));
        jLabel1.setText("x16");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 12, -1, -1));

        jLabel2.setBackground(new java.awt.Color(197, 188, 209));
        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(254, 254, 254));
        jLabel2.setText("x18");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 74, -1, -1));

        jLabel3.setBackground(new java.awt.Color(197, 188, 209));
        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(254, 254, 254));
        jLabel3.setText("x17");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 43, -1, -1));

        jLabel4.setBackground(new java.awt.Color(197, 188, 209));
        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(254, 254, 254));
        jLabel4.setText("x19");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 105, -1, -1));

        jLabel5.setBackground(new java.awt.Color(197, 188, 209));
        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(254, 254, 254));
        jLabel5.setText("x20");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 136, -1, -1));

        jLabel6.setBackground(new java.awt.Color(197, 188, 209));
        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(254, 254, 254));
        jLabel6.setText("x21");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 167, -1, -1));

        jLabel7.setBackground(new java.awt.Color(197, 188, 209));
        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(254, 254, 254));
        jLabel7.setText("x22");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 198, -1, -1));

        jLabel8.setBackground(new java.awt.Color(197, 188, 209));
        jLabel8.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(254, 254, 254));
        jLabel8.setText("x23");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 229, -1, -1));

        jLabel9.setBackground(new java.awt.Color(197, 188, 209));
        jLabel9.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(254, 254, 254));
        jLabel9.setText("x24");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, -1, -1));

        jLabel10.setBackground(new java.awt.Color(197, 188, 209));
        jLabel10.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(254, 254, 254));
        jLabel10.setText("x25");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 291, -1, -1));

        jLabel11.setBackground(new java.awt.Color(197, 188, 209));
        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(254, 254, 254));
        jLabel11.setText("x26");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 322, -1, -1));

        jLabel12.setBackground(new java.awt.Color(197, 188, 209));
        jLabel12.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(254, 254, 254));
        jLabel12.setText("x27");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 353, -1, -1));

        jLabel13.setBackground(new java.awt.Color(197, 188, 209));
        jLabel13.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(254, 254, 254));
        jLabel13.setText("x28");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 384, -1, -1));

        jLabel14.setBackground(new java.awt.Color(197, 188, 209));
        jLabel14.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(254, 254, 254));
        jLabel14.setText("x29");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 415, -1, -1));

        jLabel15.setBackground(new java.awt.Color(197, 188, 209));
        jLabel15.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(254, 254, 254));
        jLabel15.setText("x30");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 446, -1, -1));

        jLabel16.setBackground(new java.awt.Color(197, 188, 209));
        jLabel16.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(254, 254, 254));
        jLabel16.setText("x31");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 477, -1, -1));

        jTableMemory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Word Address", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableMemory);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 290, 340));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBoxMemoryFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DEC", "HEX" }));
        jComboBoxMemoryFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMemoryFilterActionPerformed(evt);
            }
        });
        jPanel4.add(jComboBoxMemoryFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 70, -1));

        addlbl1.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        addlbl1.setForeground(new java.awt.Color(119, 178, 229));
        addlbl1.setText("xx");
        jPanel4.add(addlbl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        jButtonApplyFilterSettings.setText("Apply");
        jButtonApplyFilterSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyFilterSettingsActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonApplyFilterSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 70, -1));

        jTxtRightMemLimit.setText("jTextField1");
        jPanel4.add(jTxtRightMemLimit, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 75, 25));

        addlbl2.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        addlbl2.setForeground(new java.awt.Color(119, 178, 229));
        addlbl2.setText("xx");
        jPanel4.add(addlbl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, -1, -1));

        jTxtLeftMemLimit.setText("jTextField1");
        jPanel4.add(jTxtLeftMemLimit, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 75, 25));

        jLabel56.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(134, 156, 187));
        jLabel56.setText("Start Address:");
        jPanel4.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        jLabel57.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(134, 156, 187));
        jLabel57.setText("End Address:");
        jPanel4.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 20));

        infoLbl.setFont(new java.awt.Font("Ubuntu", 3, 12)); // NOI18N
        infoLbl.setForeground(new java.awt.Color(155, 75, 75));
        infoLbl.setText("*Maximum address value is 0x40000 ");
        jPanel4.add(infoLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, -1, -1));

        jLabelError.setFont(new java.awt.Font("Ubuntu", 3, 12)); // NOI18N
        jLabelError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelError.setText("ERROR!");
        jPanel4.add(jLabelError, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 290, -1));

        jLabel54.setText("Limits base");
        jPanel4.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, -1, 30));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 360, 290, 150));

        jComboBoxMemoryBase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DEC", "BIN", "HEX" }));
        jComboBoxMemoryBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMemoryBaseActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBoxMemoryBase, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 560, 70, -1));

        reg_base_selection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DEC", "BIN", "HEX" }));
        reg_base_selection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_base_selectionActionPerformed(evt);
            }
        });
        jPanel1.add(reg_base_selection, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 530, 70, -1));

        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bg.png"))); // NOI18N
        jPanel1.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, -1, -1));

        jPanel3.setAlignmentX(0.4F);
        jPanel3.setAlignmentY(0.4F);
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        x0_val.setBackground(new java.awt.Color(128, 185, 236));
        x0_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x0_val.setForeground(new java.awt.Color(70, 70, 70));
        x0_val.setText("jTextField1");
        x0_val.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                x0_valActionPerformed(evt);
            }
        });
        jPanel3.add(x0_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 12, 238, -1));

        x1_val.setBackground(new java.awt.Color(170, 170, 170));
        x1_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x1_val.setForeground(new java.awt.Color(70, 70, 70));
        x1_val.setText("jTextField1");
        jPanel3.add(x1_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 43, 238, -1));

        x3_val.setBackground(new java.awt.Color(170, 170, 170));
        x3_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x3_val.setForeground(new java.awt.Color(70, 70, 70));
        x3_val.setText("jTextField1");
        jPanel3.add(x3_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 105, 238, -1));

        x2_val.setBackground(new java.awt.Color(128, 185, 236));
        x2_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x2_val.setForeground(new java.awt.Color(70, 70, 70));
        x2_val.setText("jTextField1");
        jPanel3.add(x2_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 74, 238, -1));

        x4_val.setBackground(new java.awt.Color(128, 185, 236));
        x4_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x4_val.setForeground(new java.awt.Color(70, 70, 70));
        x4_val.setText("jTextField1");
        jPanel3.add(x4_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 136, 238, -1));

        x5_val.setBackground(new java.awt.Color(170, 170, 170));
        x5_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x5_val.setForeground(new java.awt.Color(70, 70, 70));
        x5_val.setText("jTextField1");
        jPanel3.add(x5_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 167, 238, -1));

        x6_val.setBackground(new java.awt.Color(128, 185, 236));
        x6_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x6_val.setForeground(new java.awt.Color(70, 70, 70));
        x6_val.setText("jTextField1");
        jPanel3.add(x6_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 198, 238, -1));

        x7_val.setBackground(new java.awt.Color(170, 170, 170));
        x7_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x7_val.setForeground(new java.awt.Color(70, 70, 70));
        x7_val.setText("jTextField1");
        jPanel3.add(x7_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 229, 238, -1));

        x8_val.setBackground(new java.awt.Color(128, 185, 236));
        x8_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x8_val.setForeground(new java.awt.Color(70, 70, 70));
        x8_val.setText("jTextField1");
        jPanel3.add(x8_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 260, 238, -1));

        x9_val.setBackground(new java.awt.Color(170, 170, 170));
        x9_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x9_val.setForeground(new java.awt.Color(70, 70, 70));
        x9_val.setText("jTextField1");
        jPanel3.add(x9_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 291, 238, -1));

        x10_val.setBackground(new java.awt.Color(128, 185, 236));
        x10_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x10_val.setForeground(new java.awt.Color(70, 70, 70));
        x10_val.setText("jTextField1");
        jPanel3.add(x10_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 322, 238, -1));

        x11_val.setBackground(new java.awt.Color(170, 170, 170));
        x11_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x11_val.setForeground(new java.awt.Color(70, 70, 70));
        x11_val.setText("jTextField1");
        jPanel3.add(x11_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 353, 238, -1));

        x12_val.setBackground(new java.awt.Color(128, 185, 236));
        x12_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x12_val.setForeground(new java.awt.Color(70, 70, 70));
        x12_val.setText("jTextField1");
        jPanel3.add(x12_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 384, 238, -1));

        x13_val.setBackground(new java.awt.Color(170, 170, 170));
        x13_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x13_val.setForeground(new java.awt.Color(70, 70, 70));
        x13_val.setText("jTextField1");
        jPanel3.add(x13_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 415, 238, -1));

        x14_val.setBackground(new java.awt.Color(128, 185, 236));
        x14_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x14_val.setForeground(new java.awt.Color(70, 70, 70));
        x14_val.setText("jTextField1");
        jPanel3.add(x14_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 446, 238, -1));

        x15_val.setBackground(new java.awt.Color(170, 170, 170));
        x15_val.setFont(new java.awt.Font("Ubuntu", 3, 14)); // NOI18N
        x15_val.setForeground(new java.awt.Color(70, 70, 70));
        x15_val.setText("jTextField1");
        jPanel3.add(x15_val, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 477, 238, -1));

        jLabel35.setBackground(new java.awt.Color(197, 188, 209));
        jLabel35.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(254, 254, 254));
        jLabel35.setText("x0");
        jPanel3.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 12, -1, -1));

        jLabel36.setBackground(new java.awt.Color(197, 188, 209));
        jLabel36.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(254, 254, 254));
        jLabel36.setText("x2");
        jPanel3.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 74, -1, -1));

        jLabel37.setBackground(new java.awt.Color(197, 188, 209));
        jLabel37.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(254, 254, 254));
        jLabel37.setText("x1");
        jPanel3.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 43, -1, -1));

        jLabel38.setBackground(new java.awt.Color(197, 188, 209));
        jLabel38.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(254, 254, 254));
        jLabel38.setText("x3");
        jPanel3.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 105, -1, -1));

        jLabel39.setBackground(new java.awt.Color(197, 188, 209));
        jLabel39.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(254, 254, 254));
        jLabel39.setText("x4");
        jPanel3.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 136, -1, -1));

        jLabel40.setBackground(new java.awt.Color(197, 188, 209));
        jLabel40.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(254, 254, 254));
        jLabel40.setText("x5");
        jPanel3.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 167, -1, -1));

        jLabel41.setBackground(new java.awt.Color(197, 188, 209));
        jLabel41.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(254, 254, 254));
        jLabel41.setText("x6");
        jPanel3.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 198, -1, -1));

        jLabel42.setBackground(new java.awt.Color(197, 188, 209));
        jLabel42.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(254, 254, 254));
        jLabel42.setText("x7");
        jPanel3.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 229, -1, -1));

        jLabel43.setBackground(new java.awt.Color(197, 188, 209));
        jLabel43.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(254, 254, 254));
        jLabel43.setText("x8");
        jPanel3.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, -1, -1));

        jLabel44.setBackground(new java.awt.Color(197, 188, 209));
        jLabel44.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(254, 254, 254));
        jLabel44.setText("x9");
        jPanel3.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 291, -1, -1));

        jLabel45.setBackground(new java.awt.Color(197, 188, 209));
        jLabel45.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(254, 254, 254));
        jLabel45.setText("x10");
        jPanel3.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 322, -1, -1));

        jLabel46.setBackground(new java.awt.Color(197, 188, 209));
        jLabel46.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(254, 254, 254));
        jLabel46.setText("x11");
        jPanel3.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 353, -1, -1));

        jLabel47.setBackground(new java.awt.Color(197, 188, 209));
        jLabel47.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(254, 254, 254));
        jLabel47.setText("x12");
        jPanel3.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 384, -1, -1));

        jLabel48.setBackground(new java.awt.Color(197, 188, 209));
        jLabel48.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(254, 254, 254));
        jLabel48.setText("x13");
        jPanel3.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 415, -1, -1));

        jLabel49.setBackground(new java.awt.Color(197, 188, 209));
        jLabel49.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(254, 254, 254));
        jLabel49.setText("x14");
        jPanel3.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 446, -1, -1));

        jLabel50.setBackground(new java.awt.Color(197, 188, 209));
        jLabel50.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(254, 254, 254));
        jLabel50.setText("x15");
        jPanel3.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 477, -1, -1));

        jLabel17.setFont(new java.awt.Font("Ubuntu", 3, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(254, 254, 254));
        jLabel17.setText("Registers base");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 530, 140, -1));

        jLabel55.setFont(new java.awt.Font("Ubuntu", 3, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(254, 254, 254));
        jLabel55.setText("Memory base");
        jPanel3.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 560, -1, -1));

        jLabel51.setFont(new java.awt.Font("Ubuntu", 3, 18)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(254, 254, 254));
        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bg.png"))); // NOI18N
        jPanel3.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, -10, -1, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, -1, -1));

        jlabel_simulation_end_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/led_verde.png"))); // NOI18N
        getContentPane().add(jlabel_simulation_end_icon, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 470, 40, 40));

        jLabe_simulation_end_txt.setFont(new java.awt.Font("Ubuntu", 3, 18)); // NOI18N
        jLabe_simulation_end_txt.setForeground(new java.awt.Color(254, 254, 254));
        jLabe_simulation_end_txt.setText("Simulation End");
        getContentPane().add(jLabe_simulation_end_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 480, 140, -1));

        jLabel58.setFont(new java.awt.Font("Ubuntu", 3, 18)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(254, 254, 254));
        jLabel58.setText("Line: ");
        getContentPane().add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 480, 50, -1));

        jLabelLineCounter.setFont(new java.awt.Font("Ubuntu", 3, 18)); // NOI18N
        jLabelLineCounter.setForeground(new java.awt.Color(51, 137, 156));
        jLabelLineCounter.setText("0");
        getContentPane().add(jLabelLineCounter, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 480, 70, -1));

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bg.png"))); // NOI18N
        jLabel52.setText("jLabel52");
        getContentPane().add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel59.setText("jLabel59");
        getContentPane().add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 480, -1, -1));

        jMenu1.setText("File");

        jMenuIOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuIOpen.setText("Open");
        jMenuIOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuIOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuIOpen);

        jMenuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuSave.setText("Save");
        jMenuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuSave);

        jMenuSaveAs.setText("Save As");
        jMenuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuSaveAs);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Help");

        jMenuItem2.setText("How to use");
        jMenu3.add(jMenuItem2);

        jMenuItem4.setText("RISC-V info");
        jMenu3.add(jMenuItem4);

        jMenuItem5.setText("Supported Instructions");
        jMenu3.add(jMenuItem5);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("About");

        jMenuItem6.setText("Version");
        jMenu4.add(jMenuItem6);

        jMenuItem7.setText("License");
        jMenu4.add(jMenuItem7);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cleanMemoryView() {
        memoryList.clear();
        setTableData();
        updateTable();
    }
    private void restart_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restart_buttonActionPerformed
        // TODO add your handling code here:
        restartWindowSimulationValues();
        jLabe_simulation_end_txt.setVisible(false);
        jlabel_simulation_end_icon.setVisible(false);
        line_simulation_counter = 0;
        jLabelLineCounter.setText(String.valueOf(line_simulation_counter));
        
    }//GEN-LAST:event_restart_buttonActionPerformed

    private void restartWindowSimulationValues() {
        sim.mem.clearMemory();
        cleanMemoryView();
        sim.reg.initRegisters();
        updateRegistersView();
    }
    private void checkSintaxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkSintaxButtonActionPerformed
        // TODO add your handling code here:

        makeAnalysis();
    }//GEN-LAST:event_checkSintaxButtonActionPerformed

    private void execute_all_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_execute_all_buttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_execute_all_buttonActionPerformed

    private void execute_all_buttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_execute_all_buttonMouseClicked
        jLabe_simulation_end_txt.setVisible(false);
        restartWindowSimulationValues();
        if (currentPath.equals("none")) {
            saveAsAction();
        }
        makeAnalysis();
        if (!rvanalysis.getErrorsFlag()) {

            sim.startSimulation(rvanalysis.getInstructionBuffer(), rvanalysis.getLineCounter(), EXEC_ALL);
            updateRegistersView();
            updateMemoryContentView();
            jLabe_simulation_end_txt.setVisible(true);
        }

    }//GEN-LAST:event_execute_all_buttonMouseClicked

    private void reg_base_selectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_base_selectionActionPerformed
        // TODO add your handling code here:
        int item = reg_base_selection.getSelectedIndex();
        switch (item) {
            case 0:
                reg_base_selection_value = 10;
                break;
            case 1:
                reg_base_selection_value = 2;
                break;
            case 2:
                reg_base_selection_value = 16;
                break;
        }
        updateRegistersView();
    }//GEN-LAST:event_reg_base_selectionActionPerformed

    private void x16_valActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_x16_valActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_x16_valActionPerformed

    private void x0_val1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_x0_val1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_x0_val1ActionPerformed

    private void x0_valActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_x0_valActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_x0_valActionPerformed

    private void jMenuIOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuIOpenActionPerformed

        updateTXTfield = true;
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File f = chooser.getSelectedFile();
        if (f != null) {
            String filename = f.getAbsolutePath();
            currentPath = filename;
            loadFileContentToTxtArea(filename);
        }

    }//GEN-LAST:event_jMenuIOpenActionPerformed

    private void txt_areaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_areaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_areaKeyTyped

    private void txt_areaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_areaKeyPressed
        // TODO add your handling code here:

        int key = evt.getKeyCode();

        if (key == evt.VK_ENTER || (txt_area.getLineCount() < lastLineCount)) {
            lastLineCount = txt_area.getLineCount();
            updateTXTfield = true;
        } else {
            updateTXTfield = false;
        }
    }//GEN-LAST:event_txt_areaKeyPressed

    private void jMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveActionPerformed
        // TODO add your handling code here

        if (currentPath.equals("none")) {
            saveAsAction();
        } else {
            saveTxtArea(currentPath);
        }

    }//GEN-LAST:event_jMenuSaveActionPerformed

    private void jMenuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveAsActionPerformed
        // TODO add your handling code here:
        saveAsAction();
    }//GEN-LAST:event_jMenuSaveAsActionPerformed

    private void jComboBoxMemoryFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMemoryFilterActionPerformed
        base_selection_filter = jComboBoxMemoryFilter.getSelectedIndex();
        infoMsgLbl();

    }//GEN-LAST:event_jComboBoxMemoryFilterActionPerformed

    private void jButtonApplyFilterSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApplyFilterSettingsActionPerformed
        memRightLimit = jTxtRightMemLimit.getText();
        memLeftLimit = jTxtLeftMemLimit.getText();
        if (checkLimits()) {
            leftmemLimit = Integer.valueOf(memLeftLimit);
            rightmemLimit = Integer.valueOf(memRightLimit);
            if (is4Multiply()) {
                updateMemoryContentView();
            }
        }
        jLabelError.setText(ERROR_MSG);
    }//GEN-LAST:event_jButtonApplyFilterSettingsActionPerformed

    private boolean is4Multiply() {// true is ok, false is bad
        boolean isOk = true;
        if (leftmemLimit % 4 != 0) {
            System.out.println("Error: Invalid address need to be multiple of 4");
            ERROR_MSG = "Error: Invalid address need to be multiple of 4";
            isOk = false;
        }
        if (rightmemLimit % 4 != 0) {
            System.out.println("Error: Invalid address need to be multiple of 4");
            ERROR_MSG = "Error: Invalid address need to be multiple of 4";
            isOk = false;
        }
        return isOk;
    }
    private void jComboBoxMemoryBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMemoryBaseActionPerformed
        int item = jComboBoxMemoryBase.getSelectedIndex();
        switch (item) {
            case 0:
                mem_base_selection_value = 10;
                break;
            case 1:
                mem_base_selection_value = 2;
                break;
            case 2:
                mem_base_selection_value = 16;
                break;
        }
        updateMemoryContentView();
    }//GEN-LAST:event_jComboBoxMemoryBaseActionPerformed

    private void next_step_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next_step_buttonActionPerformed
        // TODO add your handling code here:
        if (EXEC_STEP_STARTED == 0) {
            jLabe_simulation_end_txt.setVisible(false);
            restartWindowSimulationValues();
            if (currentPath.equals("none")) {
                saveAsAction();
            }
            makeAnalysis();
            if (!rvanalysis.getErrorsFlag()) {
                EXEC_STEP_STARTED = 1;
                sim.startSimulation(rvanalysis.getInstructionBuffer(), rvanalysis.getLineCounter(), EXEC_STEP);
                updateRegistersView();
                updateMemoryContentView();
                line_simulation_counter++;
                jLabelLineCounter.setText(String.valueOf(line_simulation_counter));
            }

        } else {
            sim.step_exec();
            if (sim.end_flag) {
                jLabe_simulation_end_txt.setVisible(true);
                jlabel_simulation_end_icon.setVisible(true);
            } else {
                updateRegistersView();
                updateMemoryContentView();
                line_simulation_counter++;
                jLabelLineCounter.setText(String.valueOf(line_simulation_counter));
            }
        }
    }//GEN-LAST:event_next_step_buttonActionPerformed

    private void updateMemoryContentView() {
        memoryList.clear();
        fillMemoryList();
        setTableData();
    }

    private void saveAsAction() {
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(null);
        File f = chooser.getSelectedFile();

        if (f != null) {
            String filename = f.getAbsolutePath();
            currentPath = filename;
            saveTxtArea(currentPath);
        }
    }

    private void loadFileContentToTxtArea(String file_name_path) {
        try (BufferedReader br = new BufferedReader(new FileReader(file_name_path))) {
            String line_first;
            txt_area.setText("");
            while ((line_first = br.readLine()) != null) {
                txt_area.append(line_first);
                txt_area.append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(RiscV_Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean checkLimits() {
        boolean isOk = false;
        if (base_selection_filter == 0 && decLimitIsOk()) {
            isOk = true;
        } else if (base_selection_filter == 1 && hexLimitIsOk()) {
            isOk = true;
        } else {
            isOk = false;
        }
        return isOk;

    }

    private boolean hexLimitIsOk() {
        boolean isOk = false;
        if (checkHexFormat(memLeftLimit) && checkHexFormat(memRightLimit)) {
            int rl = Integer.parseInt(memRightLimit, 16);
            int ll = Integer.parseInt(memLeftLimit, 16);
            memRightLimit = String.valueOf(rl);
            memLeftLimit = String.valueOf(ll);
            isOk = decLimitIsOk();
        } else {
            ERROR_MSG = "Error memory address format";
        }
        return isOk;
    }

    private boolean decLimitIsOk() {
        boolean isOk = false;
        if (isNumber(memLeftLimit) && isNumber(memRightLimit)) {
            int leftNum = Integer.valueOf(memLeftLimit);
            int RightNum = Integer.valueOf(memRightLimit);
            if (leftNum <= RightNum) {
                if (RightNum <= RIGHT_MEM_LIMIT) {
                    isOk = true;
                    ERROR_MSG = "";
                } else {
                    ERROR_MSG = "Error on memory address limits";
                }
            } else {
                ERROR_MSG = "Start memory address limit is bigger than end address limit";
            }

        } else {
            ERROR_MSG = "Error memory address format";
        }
        return isOk;
    }

    private boolean checkHexFormat(String number_) {
        boolean isHexNumber = true;
        String number = number_.toLowerCase();
        char numberChar;
        for (int i = 0; i < number.length(); i++) {
            numberChar = number.charAt(i);
            switch (numberChar) {

                case '0':
                    break;
                case '1':
                    break;
                case '2':
                    break;
                case '3':
                    break;
                case '4':
                    break;
                case '5':
                    break;
                case '6':
                    break;
                case '7':
                    break;
                case '8':
                    break;
                case '9':
                    break;
                case 'a':
                    break;
                case 'b':
                    break;
                case 'c':
                    break;
                case 'd':
                    break;
                case 'e':
                    break;
                case 'f':
                    break;
                default:
                    isHexNumber = false;
                    break;
            }

        }
        return isHexNumber;
    }

    private boolean isNumber(String number) {
        boolean isNumber = true;
        char numberChar;
        for (int i = 0; i < number.length(); i++) {
            numberChar = number.charAt(i);
            switch (numberChar) {

                case '0':
                    break;
                case '1':
                    break;
                case '2':
                    break;
                case '3':
                    break;
                case '4':
                    break;
                case '5':
                    break;
                case '6':
                    break;
                case '7':
                    break;
                case '8':
                    break;
                case '9':
                    break;
                default:
                    isNumber = false;
                    break;
            }

        }
        return isNumber;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addlbl1;
    private javax.swing.JLabel addlbl2;
    private javax.swing.JButton checkSintaxButton;
    private javax.swing.JTextArea error_log_area;
    private javax.swing.JButton execute_all_button;
    private javax.swing.JLabel infoLbl;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonApplyFilterSettings;
    private javax.swing.JComboBox<String> jComboBoxMemoryBase;
    private javax.swing.JComboBox<String> jComboBoxMemoryFilter;
    private javax.swing.JLabel jLabe_simulation_end_txt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelLineCounter;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuIOpen;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenuItem jMenuSaveAs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableMemory;
    private javax.swing.JTextField jTxtLeftMemLimit;
    private javax.swing.JTextField jTxtRightMemLimit;
    private javax.swing.JLabel jlabel_simulation_end_icon;
    private javax.swing.JButton next_step_button;
    private javax.swing.JComboBox<String> reg_base_selection;
    private javax.swing.JButton restart_button;
    private javax.swing.JScrollPane scrollPaneTxt;
    private javax.swing.JButton step_back_button;
    private javax.swing.JTextArea txt_area;
    private javax.swing.JTextField x0_val;
    private javax.swing.JTextField x0_val1;
    private javax.swing.JTextField x10_val;
    private javax.swing.JTextField x10_val1;
    private javax.swing.JTextField x11_val;
    private javax.swing.JTextField x11_val1;
    private javax.swing.JTextField x12_val;
    private javax.swing.JTextField x12_val1;
    private javax.swing.JTextField x13_val;
    private javax.swing.JTextField x13_val1;
    private javax.swing.JTextField x14_val;
    private javax.swing.JTextField x14_val1;
    private javax.swing.JTextField x15_val;
    private javax.swing.JTextField x15_val1;
    private javax.swing.JTextField x16_val;
    private javax.swing.JTextField x17_val;
    private javax.swing.JTextField x18_val;
    private javax.swing.JTextField x19_val;
    private javax.swing.JTextField x1_val;
    private javax.swing.JTextField x1_val1;
    private javax.swing.JTextField x20_val;
    private javax.swing.JTextField x21_val;
    private javax.swing.JTextField x22_val;
    private javax.swing.JTextField x23_val;
    private javax.swing.JTextField x24_val;
    private javax.swing.JTextField x25_val;
    private javax.swing.JTextField x26_val;
    private javax.swing.JTextField x27_val;
    private javax.swing.JTextField x28_val;
    private javax.swing.JTextField x29_val;
    private javax.swing.JTextField x2_val;
    private javax.swing.JTextField x2_val1;
    private javax.swing.JTextField x30_val;
    private javax.swing.JTextField x31_val;
    private javax.swing.JTextField x3_val;
    private javax.swing.JTextField x3_val1;
    private javax.swing.JTextField x4_val;
    private javax.swing.JTextField x4_val1;
    private javax.swing.JTextField x5_val;
    private javax.swing.JTextField x5_val1;
    private javax.swing.JTextField x6_val;
    private javax.swing.JTextField x6_val1;
    private javax.swing.JTextField x7_val;
    private javax.swing.JTextField x7_val1;
    private javax.swing.JTextField x8_val;
    private javax.swing.JTextField x8_val1;
    private javax.swing.JTextField x9_val;
    private javax.swing.JTextField x9_val1;
    // End of variables declaration//GEN-END:variables
}
