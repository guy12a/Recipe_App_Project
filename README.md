# 🍳 Recipe App

An Android application for organizing, editing, and discovering recipes. The project is built with **Kotlin** and **Jetpack Compose** as a way to explore modern Android development while creating a practical cooking companion.

The app stores recipes locally, organizes them into cookbooks and tags, and provides a fast, responsive browsing experience through in-memory indexing.

## Features

### Recipe Management

* Create, edit, and delete recipes
* Store recipes locally using JSON serialization
* Import recipes exported from **Umami**
* Persistent local storage

### Organization

* Organize recipes into multiple cookbooks
* Categorize recipes with tags
* Fast recipe lookup using in-memory `HashMap` indexes
* Bulk cookbook operations based on tags

### Navigation & UI

* Built with **Jetpack Compose**
* Material Design 3 interface
* Navigation between Home, Cookbooks, and Recipe pages
* Consistent typography and reusable UI components
* Modal bottom sheets for editing and management actions

## Project Structure

The application centers around a search/indexing utility that maintains several lookup tables:

* **Recipe ID → Recipe** for constant-time recipe retrieval
* **Cookbook → Recipe IDs** for efficient cookbook browsing
* **Tag → Recipe IDs** for fast filtering by tags

This approach minimizes repeated searches and keeps most common operations close to **O(1)** or proportional only to the number of matching recipes.

## Technologies

* Kotlin
* Jetpack Compose
* Material 3
* Android Navigation
* Kotlin Serialization
* JSON local storage

## Planned Features

The following features are currently planned or in development:

* Improved ingredient and instruction formatting
* Advanced recipe search and filtering

  * Name
  * Tags
  * Rating
  * Cookbook
* Complete recipe editor

  * Ingredients
  * Instructions
  * Tags
  * Metadata
* Ingredient scaling (½×, 2×, etc.)
* Pantry management
* Grocery list generation
* Metric ↔ Imperial/Volume conversions
* Cooking Mode

  * Step-by-step instructions
  * Built-in timers
  * Full-screen mode
* Recipe extraction from supported cooking websites

## Motivation

This project serves both as a useful everyday application and as a learning platform for Android development. It focuses on building clean architecture, reusable Compose components, efficient data structures, and a polished user experience while continually expanding functionality.
