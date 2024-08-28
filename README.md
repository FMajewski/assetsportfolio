# Crypto Portfolio App

This is a Crypto Portfolio application that allows you to manage your cryptocurrency investments effectively.

## Features

- **Add or Delete Crypto**: You can add or remove cryptocurrencies from your portfolio. The app will automatically summarize all your holdings by name.
- **Portfolio Summary**: Get a complete overview of your portfolio, with the total value of all your cryptocurrencies combined.
- **Currency Conversion**: Display the value of your portfolio in different currencies (e.g., USD, EUR, PLN) with real-time conversion rates.
- **Top 15 Cryptocurrencies**: View the top 15 cryptocurrencies by market cap with their current prices and 24-hour change.
- **Bitcoin Analysis by ChatGPT**: The app provides an analysis of Bitcoin based on real-time data, generated by ChatGPT.
- **Price Alerts**: Set price alerts on your cryptocurrencies, and receive email notifications when the target price is reached.
- **Automatic Price Updates**: Prices are updated every 5 minutes to keep your portfolio up to date.

## How Does It Work?

- **CoinGecko API**: The app connects to the CoinGecko API to fetch the latest cryptocurrency prices.
- **OpenAI API**: It sends Bitcoin data to OpenAI's API and gets back an analysis from ChatGPT.
- **Currency Rates API**: The app retrieves currency exchange rates to convert your portfolio value into different currencies.
- **Database Integration**: Cryptocurrencies and alerts are stored in a MySQL database for persistent storage.
- **Caching**: Prices are cached and updated every 5 minutes to optimize performance.
- **Email Notifications**: Alerts are triggered and sent when your set price targets are met.

## Technology Stack

- **Java**
- **Spring Framework**
- **Spring Boot**
- **Maven**
- **MySQL** for database management
- **WebClient** for making API calls
- **Thymeleaf** for rendering the frontend
- **JavaMailSender** for sending email notifications
- **OpenAI API** for Bitcoin analysis
- **CoinGecko API** for fetching cryptocurrency prices
- **Currency Rates API** for currency conversion

## Installation and Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/crypto-portfolio-app.git
