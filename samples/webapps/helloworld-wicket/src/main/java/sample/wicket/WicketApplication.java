package sample.wicket;

import org.apache.wicket.Page;
import org.apache.tuscany.sca.wicket.TuscanyInjector;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see com.mycompany.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{    

    @Override
    protected void init()
    {
        addComponentInstantiationListener(new TuscanyInjector(this));
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class< ? extends Page> getHomePage()
    {
        return HomePage.class;
    }
}