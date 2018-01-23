package by.nalivajr.margo.exceptions;

import by.nalivajr.margo.annonatations.AutoInjectFragment;

/**
 * Created by Sergey Nalivko.
 * email: snalivko93@gmail.com
 */
public class NotAnnotatedFragmentUsedException extends RuntimeException {

    public NotAnnotatedFragmentUsedException() {
        super("Target fragment should be annotated with %s and specify layout resource id " + AutoInjectFragment.class );
    }
}
