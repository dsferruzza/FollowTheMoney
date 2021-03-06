Follow The Money
================

A simple web application to track expenses.

## Why?

I wanted to make a small but realistic projet to try out **Play Framework in Scala**.
I was tracking my expenses using a SQL database by hand, so I decided to build a UI on top of it.

**This is my first application with Play, so I'd love to get some feedback/advices!**

## How to run

- install a JDK and Play Framework (see http://www.playframework.com/documentation/2.3.x/Installing)
- setup a PostgreSQL database
- add a DB access to `conf/application.conf` or override some of its keys while lauching (see http://www.playframework.com/documentation/2.3.x/ProductionConfiguration)
- `activator run` (dev) or `activator stage` and then `./target/universal/stage/bin/ftm` (prod)

## Screenshots

- [Home](screenshots/screenshot1.png)
- [Category](screenshots/screenshot2.png)
- [Expense](screenshots/screenshot3.png)
- [Analyze](screenshots/screenshot4.png)

## License

The MIT License (MIT) Copyright (c) 2014 David Sferruzza

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
