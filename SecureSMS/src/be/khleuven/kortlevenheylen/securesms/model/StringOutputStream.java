package be.khleuven.kortlevenheylen.securesms.model;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream
{
    private StringBuilder m_string;

    public StringOutputStream()
    {
        m_string = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException
    {
        m_string.append( (char) b );
    }

    @Override
    public String toString()
    {
        return m_string.toString();
    }
}