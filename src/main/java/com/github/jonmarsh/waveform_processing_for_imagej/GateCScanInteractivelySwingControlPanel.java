package com.github.jonmarsh.waveform_processing_for_imagej;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 *
 * @author Jon N. Marsh
 */

public class GateCScanInteractivelySwingControlPanel extends javax.swing.JPanel
{

	/**
	 * Creates new form GateCScanInteractivelySwingControlPanel
	 */
	public GateCScanInteractivelySwingControlPanel()
	{
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        searchStartPointTextField = new JTextField();
        jLabel1 = new JLabel();
        offsetTextField = new JTextField();
        thresholdTextField = new JTextField();
        gateLengthTextField = new JTextField();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        searchBackwardsCheckbox = new JCheckBox();
        smoothGatesButton = new JButton();
        createGatesButton = new JButton();
        filterComboBox = new JComboBox();
        jLabel5 = new JLabel();
        smoothingRadiusTextField = new JTextField();
        jLabel6 = new JLabel();
        gateApplicationComboBox = new JComboBox();
        jLabel7 = new JLabel();
        jPanel1 = new JPanel();
        outputGatePositionsCheckbox = new JCheckBox();
        outputGatedRegionsCheckbox = new JCheckBox();
        outputGatedWaveformsCheckbox = new JCheckBox();
        outputGateROIsCheckbox = new JCheckBox();
        cancelButton = new JButton();
        okButton = new JButton();
        detectionMethodComboBox = new JComboBox();
        jLabel8 = new JLabel();

        setBorder(BorderFactory.createEtchedBorder());

        searchStartPointTextField.setColumns(6);
        searchStartPointTextField.setText("0");
        searchStartPointTextField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                searchStartPointTextFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("Start searching at point");

        offsetTextField.setColumns(6);
        offsetTextField.setText("0");

        thresholdTextField.setColumns(6);
        thresholdTextField.setText("0.000");
        thresholdTextField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                thresholdTextFieldActionPerformed(evt);
            }
        });

        gateLengthTextField.setColumns(6);
        gateLengthTextField.setText("0");

        jLabel2.setText("Offset from detected border");

        jLabel3.setText("Threshold");

        jLabel4.setText("Gate length");

        searchBackwardsCheckbox.setText("Reverse search");
        searchBackwardsCheckbox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                searchBackwardsCheckboxActionPerformed(evt);
            }
        });

        smoothGatesButton.setText("Smooth");

        createGatesButton.setText("Detect");

        jLabel5.setText("Smoothing filter");

        smoothingRadiusTextField.setColumns(4);
        smoothingRadiusTextField.setText("1.0");
        smoothingRadiusTextField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                smoothingRadiusTextFieldActionPerformed(evt);
            }
        });

        jLabel6.setText("Radius");

        jLabel7.setText("Apply gating to");

        jPanel1.setBorder(BorderFactory.createTitledBorder("Output"));

        outputGatePositionsCheckbox.setSelected(true);
        outputGatePositionsCheckbox.setText("Gate start positions");
        outputGatePositionsCheckbox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                outputGatePositionsCheckboxActionPerformed(evt);
            }
        });

        outputGatedRegionsCheckbox.setSelected(true);
        outputGatedRegionsCheckbox.setText("Gated portion of waveforms");
        outputGatedRegionsCheckbox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                outputGatedRegionsCheckboxActionPerformed(evt);
            }
        });

        outputGatedWaveformsCheckbox.setSelected(true);
        outputGatedWaveformsCheckbox.setText("Entire gated waveforms");

        outputGateROIsCheckbox.setSelected(true);
        outputGateROIsCheckbox.setText("Gate ROIs");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(outputGatePositionsCheckbox)
                    .addComponent(outputGatedRegionsCheckbox)
                    .addComponent(outputGatedWaveformsCheckbox)
                    .addComponent(outputGateROIsCheckbox))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(outputGatePositionsCheckbox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputGatedRegionsCheckbox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputGatedWaveformsCheckbox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputGateROIsCheckbox))
        );

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");

        detectionMethodComboBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                detectionMethodComboBoxActionPerformed(evt);
            }
        });

        jLabel8.setText("Detection method");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, GroupLayout.Alignment.TRAILING)
                            .addComponent(createGatesButton, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(gateApplicationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(gateLengthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(offsetTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchStartPointTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(smoothGatesButton)
                            .addComponent(filterComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(smoothingRadiusTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(thresholdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(detectionMethodComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchBackwardsCheckbox)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchStartPointTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(offsetTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(gateLengthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(thresholdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(detectionMethodComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchBackwardsCheckbox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(smoothGatesButton)
                    .addComponent(createGatesButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(filterComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(smoothingRadiusTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(gateApplicationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchBackwardsCheckboxActionPerformed(ActionEvent evt)//GEN-FIRST:event_searchBackwardsCheckboxActionPerformed
    {//GEN-HEADEREND:event_searchBackwardsCheckboxActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_searchBackwardsCheckboxActionPerformed

    private void outputGatePositionsCheckboxActionPerformed(ActionEvent evt)//GEN-FIRST:event_outputGatePositionsCheckboxActionPerformed
    {//GEN-HEADEREND:event_outputGatePositionsCheckboxActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_outputGatePositionsCheckboxActionPerformed

    private void cancelButtonActionPerformed(ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void thresholdTextFieldActionPerformed(ActionEvent evt)//GEN-FIRST:event_thresholdTextFieldActionPerformed
    {//GEN-HEADEREND:event_thresholdTextFieldActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_thresholdTextFieldActionPerformed

    private void searchStartPointTextFieldActionPerformed(ActionEvent evt)//GEN-FIRST:event_searchStartPointTextFieldActionPerformed
    {//GEN-HEADEREND:event_searchStartPointTextFieldActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_searchStartPointTextFieldActionPerformed

    private void outputGatedRegionsCheckboxActionPerformed(ActionEvent evt)//GEN-FIRST:event_outputGatedRegionsCheckboxActionPerformed
    {//GEN-HEADEREND:event_outputGatedRegionsCheckboxActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_outputGatedRegionsCheckboxActionPerformed

    private void smoothingRadiusTextFieldActionPerformed(ActionEvent evt)//GEN-FIRST:event_smoothingRadiusTextFieldActionPerformed
    {//GEN-HEADEREND:event_smoothingRadiusTextFieldActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_smoothingRadiusTextFieldActionPerformed

    private void detectionMethodComboBoxActionPerformed(ActionEvent evt)//GEN-FIRST:event_detectionMethodComboBoxActionPerformed
    {//GEN-HEADEREND:event_detectionMethodComboBoxActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_detectionMethodComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JButton cancelButton;
    public JButton createGatesButton;
    public JComboBox detectionMethodComboBox;
    public JComboBox filterComboBox;
    public JComboBox gateApplicationComboBox;
    public JTextField gateLengthTextField;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JPanel jPanel1;
    public JTextField offsetTextField;
    public JButton okButton;
    public JCheckBox outputGatePositionsCheckbox;
    public JCheckBox outputGateROIsCheckbox;
    public JCheckBox outputGatedRegionsCheckbox;
    public JCheckBox outputGatedWaveformsCheckbox;
    public JCheckBox searchBackwardsCheckbox;
    public JTextField searchStartPointTextField;
    public JButton smoothGatesButton;
    public JTextField smoothingRadiusTextField;
    public JTextField thresholdTextField;
    // End of variables declaration//GEN-END:variables
}
