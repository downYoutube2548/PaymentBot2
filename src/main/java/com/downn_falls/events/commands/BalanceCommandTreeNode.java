package com.downn_falls.events.commands;

import com.downn_falls.events.commands.balance.AddCommandTreeNode;
import com.downn_falls.events.commands.balance.CheckCommandTreeNode;
import com.downn_falls.events.commands.balance.RemoveCommandTreeNode;
import com.downn_falls.events.commands.balance.SetCommandTreeNode;

public class BalanceCommandTreeNode extends CommandTreeNode {
    public BalanceCommandTreeNode(CommandTreeNode parent) {
        super(parent, "balance");

        addChild(new AddCommandTreeNode(this));
        addChild(new CheckCommandTreeNode(this));
        addChild(new RemoveCommandTreeNode(this));
        addChild(new SetCommandTreeNode(this));
    }
}
