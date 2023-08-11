package edu.rpi.legup.controller;

import edu.rpi.legup.app.GameBoardFacade;
import edu.rpi.legup.app.LegupPreferences;
import edu.rpi.legup.history.*;
import edu.rpi.legup.model.Puzzle;
import edu.rpi.legup.model.gameboard.CaseBoard;
import edu.rpi.legup.model.rules.*;
import edu.rpi.legup.model.tree.*;
import edu.rpi.legup.ui.proofeditorui.rulesview.RuleButton;
import edu.rpi.legup.ui.proofeditorui.rulesview.RulePanel;
import edu.rpi.legup.ui.proofeditorui.treeview.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static edu.rpi.legup.app.GameBoardFacade.getInstance;

public class RuleController implements ActionListener {
    protected Object lastSource;

    /**
     * RuleController Constructor creates a controller object to listen
     * to ui events from a {@link RulePanel}
     */
    public RuleController() {
        super();
    }

    /**
     * Button Pressed event occurs a when a rule button has been pressed
     *
     * @param rule rule of the button that was pressed
     */
    public void buttonPressed(Rule rule) {
        TreePanel treePanel = GameBoardFacade.getInstance().getLegupUI().getTreePanel();
        TreeView treeView = treePanel.getTreeView();
        Puzzle puzzle = getInstance().getPuzzleModule();
        TreeViewSelection selection = treeView.getSelection();
        List<TreeElementView> selectedViews = selection.getSelectedViews();




        //Work on having transition so we know which transition we are supposed to do
        BoardView boardview = puzzle.getBoardView();

        TreeTransition thisTreeTransition = (TreeTransition) boardview.getTreeElement();
        thisTreeTransition.setCurrentBoard( thisTreeTransition.getBoard() );
        if (thisTreeTransition.getParents().size() >0) {
            transition.setPrevBoard(thisTreeTransition.getParents().get(0).getBoard());
        }


        



        //----------------------------------------------------------------

        String updateErrorString = "";
        if (rule.getRuleType() == RuleType.CASE) {
            CaseRule caseRule = (CaseRule) rule;
            if (selectedViews.size() == 1) {
                TreeElementView elementView = selection.getFirstSelection();
                TreeElement element = elementView.getTreeElement();
                if (element.getType() == TreeElementType.TRANSITION) {
                    System.out.println("case 1");
                    ICommand caseRuleCommand = new ValidateCaseRuleCommand(selection, caseRule);
                    if (caseRuleCommand.canExecute()) {
                        caseRuleCommand.execute();
                        getInstance().getHistory().pushChange(caseRuleCommand);
                    }
                    else {
                        updateErrorString = caseRuleCommand.getError();
                    }
                }
                else {
                    System.out.println("case 2");
                    //THIS IS THE SENARIO WE NEED TO WATCH OUT FOR 
                    if (LegupPreferences.getInstance().getUserPref(LegupPreferences.AUTO_GENERATE_CASES).equalsIgnoreCase(Boolean.toString(true))) {
                        CaseBoard caseBoard = caseRule.getCaseBoard(element.getBoard());
                        if (caseBoard != null && caseBoard.getCount() > 0) {
                            puzzle.notifyBoardListeners(listener -> listener.onCaseBoardAdded(caseBoard));
                        }
                        else {
                            updateErrorString = "This board cannot be applied with this case rule.";
                        }
                    }
                    else {
                        updateErrorString = "Auto generated case rules are turned off in preferences.";
                    }
                }
            }
            else {
                //execute command
                ICommand caseRuleCommand = new ValidateCaseRuleCommand(selection, caseRule);
                if (caseRuleCommand.canExecute()) {
                    caseRuleCommand.execute();
                    getInstance().getHistory().pushChange(caseRuleCommand);
                }
                else {
                    updateErrorString = caseRuleCommand.getError();
                }
            }
        }
        else {
            if (rule.getRuleType() == RuleType.CONTRADICTION) {
                boolean noContradiction = ((ContradictionRule) rule).checkRule(thisTreeTransition) == this.NO_CONTRADICTION_MESSAGE; 
                TreeElementView elementView = selection.getFirstSelection();
                TreeElement element = elementView.getTreeElement();

                ICommand validate = new ValidateContradictionRuleCommand(selection, (ContradictionRule) rule);


                //This is where we check whether or not we get it correct somehow?
                //Then we need to light up the board, and the user should press to determine where the contradiction rule was caused by 


                else {
                    updateErrorString = validate.getError();
                }

                if (!noContradiction) {
                    if (selectedViews.size() == 1) {
                        TreeElementView elementView = selection.getFirstSelection();
                        TreeElement element = elementView.getTreeElement();
                        Board board = element.getBoard();
                        board.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                int x = e.getX(); // X coordinate of the click
                                int y = e.getY(); // Y coordinate of the click
                                
                                PuzzleElement puzzleElement = BoardView.getElement(e.getPoint()).getPuzzleElement();
                            }
                        });


                    boolean noneContradiction = ((ContradictionRule) rule).checkRuleAt(thisTreeTransition, puzzleElement) == this.NO_CONTRADICTION_MESSAGE;
                    if (!noneContradiction) {
                        if (validate.canExecute()) {
                            getInstance().getHistory().pushChange(validate);
                            validate.execute();
                        }
                        System.out.println("We still have that contradiction");
                    }
                    else {
                        if (validate.canExecute()) {
                            getInstance().getHistory().pushChange(validate);
                            validate.execute();
                        }   
                        System.out.println("no contradiction anymore");                     
                    }
                }
            }
            else {
                boolean def = LegupPreferences.getInstance().getUserPrefAsBool(LegupPreferences.ALLOW_DEFAULT_RULES);
                ICommand validate = def ? new ApplyDefaultDirectRuleCommand(selection, (DirectRule) rule) : new ValidateDirectRuleCommand(selection, (DirectRule) rule);
                if (validate.canExecute()) {
                    getInstance().getHistory().pushChange(validate);
                    validate.execute();
                }
                else {
                    updateErrorString = validate.getError();
                }
            }
        }
        GameBoardFacade.getInstance().getLegupUI().getTreePanel().updateError(updateErrorString);

    }


    /**
     * ICommand Performed event occurs when a rule button has been pressed
     *
     * @param e action event object
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        lastSource = e.getSource();
        RuleButton button = (RuleButton) lastSource;
        buttonPressed(button.getRule());
    }
}
