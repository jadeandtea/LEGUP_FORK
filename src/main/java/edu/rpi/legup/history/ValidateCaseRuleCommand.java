package edu.rpi.legup.history;

import static edu.rpi.legup.app.GameBoardFacade.getInstance;

import edu.rpi.legup.app.GameBoardFacade;
import edu.rpi.legup.model.Puzzle;
import edu.rpi.legup.model.rules.CaseRule;
import edu.rpi.legup.model.rules.Rule;
import edu.rpi.legup.model.tree.*;
import edu.rpi.legup.ui.proofeditorui.treeview.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ValidateCaseRuleCommand class represents a command for validating a CaseRule in the tree
 * structure. It extends the PuzzleCommand class and implements the ICommand interface.
 */
public class ValidateCaseRuleCommand extends PuzzleCommand {

    private TreeViewSelection selection;
    private CaseRule caseRule;

    private Map<TreeElement, Rule> oldRule;
    private Map<TreeTransition, TreeNode> addNode;

    /**
     * AutoCaseRuleCommand Constructor creates a command for verifying a case rule
     *
     * @param selection currently selected tree puzzleElement views that is being edited
     * @param caseRule currently selected caseRule puzzleElement view that is being edited
     */
    public ValidateCaseRuleCommand(TreeViewSelection selection, CaseRule caseRule) {
        this.selection = selection.copy();
        this.caseRule = caseRule;
        this.oldRule = new HashMap<>();
        this.addNode = new HashMap<>();
    }

    /** Executes the command to validate the CaseRule */
    @Override
    public void executeCommand() {
        Tree tree = getInstance().getTree();
        TreeView treeView = GameBoardFacade.getInstance().getLegupUI().getTreePanel().getTreeView();
        Puzzle puzzle = GameBoardFacade.getInstance().getPuzzleModule();
        final TreeViewSelection newSelection = new TreeViewSelection();

        List<TreeElementView> selectedViews = selection.getSelectedViews();
        for (TreeElementView view : selectedViews) {
            TreeElement element = view.getTreeElement();
            TreeTransition transition = (TreeTransition) element;
            oldRule.put(transition, transition.getRule());

            transition.setRule(caseRule);

            TreeNode childNode = transition.getChildNode();
            if (childNode == null) {
                childNode = addNode.get(transition);
                if (childNode == null) {
                    childNode = (TreeNode) tree.addTreeElement(transition);
                    addNode.put(transition, childNode);
                } else {
                    childNode = (TreeNode) tree.addTreeElement(transition, childNode);
                }

                final TreeNode finalNode = childNode;
                puzzle.notifyTreeListeners(listener -> listener.onTreeElementAdded(finalNode));
            }
            transition.getParents().get(0).getChildren().forEach(TreeTransition::reverify);
            newSelection.addToSelection(treeView.getElementView(childNode));
        }

        TreeElementView firstSelectedView = selection.getFirstSelection();
        final TreeElement finalTreeElement;
        if (firstSelectedView.getType() == TreeElementType.NODE) {
            TreeNodeView nodeView = (TreeNodeView) firstSelectedView;
            finalTreeElement = nodeView.getChildrenViews().get(0).getTreeElement();
        } else {
            TreeTransitionView transitionView = (TreeTransitionView) firstSelectedView;
            finalTreeElement = transitionView.getChildView().getTreeElement();
        }
        puzzle.notifyBoardListeners(listener -> listener.onTreeElementChanged(finalTreeElement));
        puzzle.notifyTreeListeners(listener -> listener.onTreeSelectionChanged(newSelection));
    }

    /**
     * Gets the reason why the command cannot be executed
     *
     * @return if command cannot be executed, returns reason for why the command cannot be executed,
     *     otherwise null if command can be executed
     */
    @Override
    public String getErrorString() {
        List<TreeElementView> selectedViews = selection.getSelectedViews();
        if (selectedViews.isEmpty()) {
            return CommandError.NO_SELECTED_VIEWS.toString();
        }

        for (TreeElementView view : selectedViews) {
            if (view.getType() == TreeElementType.NODE) {
                return CommandError.SELECTION_CONTAINS_NODE.toString();
            } else {
                TreeTransitionView transView = (TreeTransitionView) view;
                if (transView.getParentViews().size() > 1) {
                    return CommandError.CONTAINS_MERGE.toString();
                }
            }
        }
        return null;
    }

    /** Undoes the validation command, restoring the previous state */
    @Override
    public void undoCommand() {
        Puzzle puzzle = GameBoardFacade.getInstance().getPuzzleModule();

        List<TreeElementView> selectedViews = selection.getSelectedViews();
        for (TreeElementView view : selectedViews) {
            TreeElement element = view.getTreeElement();
            TreeTransition transition = (TreeTransition) element;

            transition.setRule(oldRule.get(transition));

            final TreeNode childNode = transition.getChildNode();
            if (addNode.get(transition) != null) {
                puzzle.notifyTreeListeners(listener -> listener.onTreeElementRemoved(childNode));
            }
            transition.getParents().get(0).getChildren().forEach(TreeTransition::reverify);
        }

        final TreeElement finalTreeElement = selection.getFirstSelection().getTreeElement();
        puzzle.notifyBoardListeners(listener -> listener.onTreeElementChanged(finalTreeElement));
        puzzle.notifyTreeListeners(listener -> listener.onTreeSelectionChanged(selection));
    }
}
