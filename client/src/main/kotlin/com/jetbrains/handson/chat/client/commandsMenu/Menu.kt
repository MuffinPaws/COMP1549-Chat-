package com.jetbrains.handson.chat.client.commandsMenu

object Menu {
    private const val shortMenu = "For list of all commands enter help. \nPlease enter command: "
    private const val longMenu = """
                Type 'exit' or 'quit' to close the program. 
                type 'read' to read messages
                type 'send' to type a new message (Press Enter to send it)
                type 'history' to fetch messages history
                type 'members' to list all members
                Please enter command: 
    """

    private fun print(short: Boolean = true) {
        if (short) return print(shortMenu)
        print(longMenu.trimIndent())
    }

    fun getTask(shortMenu: Boolean = true): MenuTasks {
        print(shortMenu)
        val input = MenuTasks.findTask(readln())
        when (input) {
            MenuTasks.HELP -> return getTask(shortMenu = false)
            MenuTasks.UNKNOWN -> {
                println("unknown command🥴")
                return getTask(shortMenu = false)
            }
            else -> return input
        }
    }
}