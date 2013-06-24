C4J-Eclipse-Plugin
==================
##Installation
To install the C4J-Plugin using the Eclipse update/install manager, please refer to the following link:
* https://github.com/C4J-Team/C4J-Eclipse-Plugin/raw/master/update-site/

Please make sure your Eclipse platform is up-to-date. (Required bundle version 3.8.2)

##Feature List
###Create a new C4J-Project
You can create a new C4J-Project using the "new Project" wizard of Eclipse. Just go to e.g. **File -> New -> Project -> C4J Project Wizard**. 
A project creation wizard is shown and asks you to set up your C4J-Project. The required C4J libraries and Classpath settings are added automatically.

###Convert an existing Java-Project into a C4J-Project
To convert an existing Java-Project into a C4J-Project just **right-click** your project in the Package Explorer View (alternative: Navigator or Project Explorer) and go to **Configure -> Convert to C4J Project**. A Dialog is shown and prompts you to choose the location of the C4J libraries and config files.

###Creating a new contract classes/methods
For target classes (=classes guarded by contracts) opened in the editor you can create new contracts using the shortcut **Ctrl+1**. The C4J-Plugin identifies the name of the target class under the cursor. The cursor can be either set on class or method level. If the target class has no contract up to that point, a contract creation wizard is going to be shown. In case the target class is already guarded by a contract a dialog is shown and prompts you to 
* jump to an existing contract
* jump to an existing contract method corresponding to the cursor position 
* create a new contract method corresponding to the cursor position (if a contract exists but does not contain a contract method for the selected target method)
* create a new contract for the corresponding target

###Switching between target and contract class
You can jump from target classes to the corresponding contract classes using the shortcut **Ctrl+Alt+Right** and/or **Ctrl+Alt+Left**. The C4J-Plugin identifies the name of the target class under the cursor. If exactly one contract is found this contract is going to be opened in the editor. If more than one contract is found a dialog is shown and prompts you to choose which contract class you want to open.

###Displaying contract information 
Having classes guarded by contracts one of the greatest advantages is that the contracts exactly state 
* what you need to do to use the classes correctly (preconditions), and
* what you get as a result (postconditions) - if you fulfill the preconditions -

The C4J-Plugin provides you an easy access to these contract information. To display the contract information of a target class, **point your mouse cursor to the desired class/method** opened in the editor. The corresponding conditions are going to be displayed in a lightweight pop-up dialog directly in the editor.
[![githalytics.com alpha](https://cruel-carlota.gopagoda.com/3083d7ebe6c6e8b78a9abf7ad7a889ae "githalytics.com")](http://githalytics.com/C4J-Team/C4J-Eclipse-Plugin)
