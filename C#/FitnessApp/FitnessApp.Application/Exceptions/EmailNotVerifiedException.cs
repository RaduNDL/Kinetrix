namespace FitnessApp.Application.Exceptions;

public sealed class EmailNotVerifiedException : Exception
{
    public EmailNotVerifiedException()
        : base("Verify your email before signing in. You can request a new code in the app.")
    {
    }
}
