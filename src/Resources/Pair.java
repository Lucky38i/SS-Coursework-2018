package Resources;

/**
 * This is a slight recreation of the Pair type from C++, its very basic and is used for basic purposes
 * @param <F> This is the first type to be set for the pair
 * @param <S> This is the second type to be set for the pair
 */
public class Pair<F,S>
{
    private F first;
    private S second;

    public Pair()
    {
        init();
    }

    private void init()
    {
        first = null;
        second = null;
    }


    public F getFirst()
    {
        return first;
    }

    public void setFirst(F first)
    {
        this.first = first;
    }

    public S getSecond()
    {
        return second;
    }

    public void setSecond(S second)
    {
        this.second = second;
    }
}
