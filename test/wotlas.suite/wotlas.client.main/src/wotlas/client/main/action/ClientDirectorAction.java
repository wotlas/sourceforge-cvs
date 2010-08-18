/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wotlas.client.main.action;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import wotlas.client.ClientDirector;

public final class ClientDirectorAction extends CallableSystemAction {

    public void performAction() {
        final String[] argv = new String[] { "-debug", "-base", "P:/wotlas_dev/temp/wotlas.base"};
        new Thread(new Runnable() {
            public void run() {
                ClientDirector.main(argv);
            }
        }).start();
        
    // TODO implement action body
    }

    public String getName() {
        return NbBundle.getMessage(ClientDirectorAction.class, "CTL_ClientDirectorAction");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
