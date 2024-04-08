/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

 package be.vlaanderen.rdfproto;

 import kg.apc.jmeter.JMeterPluginsUtils;
 import kg.apc.jmeter.gui.BrowseAction;
 import kg.apc.jmeter.gui.GuiBuilderHelper;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.hsqldb.persist.Log;

 import javax.swing.*;
 import java.awt.*;
 import java.io.IOException;

 public class RDFProtoGui extends AbstractConfigGui {
 
     // TODO: use full URL and change cmn version to 0.6 after it has been released
     public static final String WIKIPAGE = "RDFProto";

     private static final Logger LOGGER = LoggingManager.getLoggerForClass();
 
     private JTextField filenameField;
 
     private JTextArea checkArea;
 
     public RDFProtoGui() {
         initGui();
         initGuiValues();
     }
 
     private void initGui() {
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
 
         Container topPanel = makeTitlePanel();
 
         add(JMeterPluginsUtils.addHelpLinkToPanel(topPanel, WIKIPAGE), BorderLayout.NORTH);
         add(topPanel, BorderLayout.NORTH);
 
         JPanel mainPanel = new JPanel(new GridBagLayout());
 
         GridBagConstraints labelConstraints = new GridBagConstraints();
         labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
 
         GridBagConstraints editConstraints = new GridBagConstraints();
         editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
         editConstraints.weightx = 1.0;
         editConstraints.fill = GridBagConstraints.HORIZONTAL;
 
         int row = 0;
         addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Filename: ", JLabel.RIGHT));
         addToPanel(mainPanel, editConstraints, 1, row, filenameField = new JTextField(20));
         JButton browseButton;
         addToPanel(mainPanel, labelConstraints, 2, row, browseButton = new JButton("Browse..."));
         row++;
         GuiBuilderHelper.strechItemToComponent(filenameField, browseButton);
 
         editConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
         labelConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
 
         browseButton.addActionListener(new BrowseAction(filenameField, false));
 
         editConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
         labelConstraints.insets = new java.awt.Insets(4, 0, 0, 2);
 
         JButton checkButton;
         addToPanel(mainPanel, labelConstraints, 0, row, checkButton = new JButton("Test Protobuf file reading"));
 
         labelConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
 
         checkArea = new JTextArea();
         addToPanel(mainPanel, editConstraints, 1, row, GuiBuilderHelper.getTextAreaScrollPaneContainer(checkArea, 10));
         checkButton.addActionListener(new RDFProtoGuiAction(this));
         checkArea.setEditable(false);
         checkArea.setOpaque(false);
 
 
         JPanel container = new JPanel(new BorderLayout());
         container.add(mainPanel, BorderLayout.NORTH);
         add(container, BorderLayout.CENTER);
     }
 
     private void initGuiValues() {
         filenameField.setText("");
         checkArea.setText("");
     }
 
     private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
         constraints.gridx = col;
         constraints.gridy = row;
         panel.add(component, constraints);
     }
 
     @Override
     public String getLabelResource() {
         return "rdf_protobuf_data_set_config";
     }
 
     @Override
     public String getStaticLabel() {
         return "RDFDataCreator";
     }
 
     @Override
     public TestElement createTestElement() {
         RDFProto element = new RDFProto();
         modifyTestElement(element);
         return element;
     }
 
     @Override
     public void modifyTestElement(TestElement element) {
         configureTestElement(element);
         if (element instanceof RDFProto) {
            RDFProto rdfProto = (RDFProto) element;
             try {
                 if (!this.filenameField.getText().equals(""))
                     rdfProto.setProtobuf(this.filenameField.getText());
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }
     }
 
     @Override
     public void configure(TestElement element) {
         super.configure(element);
     }
 
     @Override
     public void clearGui() {
         super.clearGui();
         initGuiValues();
     }
 
     public JTextArea getCheckArea() {
         return checkArea;
     }
 }
 