package com.downn_falls.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.*;

public class CommandTreeNode {
    private final String id;
    private final CommandTreeNode parent;
    private final HashMap<String, CommandTreeNode> children = new HashMap<>();

    public CommandTreeNode(CommandTreeNode parent, String id) {
        this.parent = parent;
        this.id = id;
    }

    public CommandTreeNode getParent() { return parent; }
    public String getId() { return id; }
    public void addChild(CommandTreeNode child) { children.put(child.getId(), child); }
    public Collection<CommandTreeNode> getChild() { return children.values(); }
    public boolean hasChild(String id) {
        return this.children.containsKey(id.toLowerCase());
    }
    public List<CommandTreeNode> getParents() {
        List<CommandTreeNode> output = new ArrayList<>();
        CommandTreeNode current = this;
        while (current != null) {
            output.add(current);
            current = current.getParent();
        }

        Collections.reverse(output);
        return output;
    }

    public void run(SlashCommandInteractionEvent event) {
        if (children.containsKey(event.getInteraction().getSubcommandName())) {
            children.get(event.getInteraction().getSubcommandName()).run(event);
        }
    }
}
