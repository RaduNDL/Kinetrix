FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build

WORKDIR /src

COPY ["FitnessApp.slnx", "./"]
COPY ["FitnessApp.API/FitnessApp.API.csproj", "FitnessApp.API/"]
COPY ["FitnessApp.Application/FitnessApp.Application.csproj", "FitnessApp.Application/"]
COPY ["FitnessApp.Domain/FitnessApp.Domain.csproj", "FitnessApp.Domain/"]
COPY ["FitnessApp.Infrastructure/FitnessApp.Infrastructure.csproj", "FitnessApp.Infrastructure/"]

RUN dotnet restore "FitnessApp.slnx"

COPY . .

WORKDIR "/src/FitnessApp.API"

RUN dotnet publish "FitnessApp.API.csproj" --configuration Release --output /app/publish --no-restore /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime

WORKDIR /app

ENV ASPNETCORE_URLS=http://+:8080
ENV ASPNETCORE_ENVIRONMENT=Development

EXPOSE 8080

COPY --from=build /app/publish .

ENTRYPOINT ["dotnet", "FitnessApp.API.dll"]