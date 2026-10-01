using FitnessApp.Application.DTOs;

namespace FitnessApp.Application.Interfaces;

public interface IAuthService
{
    Task<RegisterResponseDto> RegisterAsync(
        RegisterRequestDto request);

    Task<AuthResponseDto> LoginAsync(
        LoginRequestDto request);
}