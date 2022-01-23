## Static Site Generator

This repo contains the start of a static site generator.
Rather than relying on conventions this static site generator is based on scripting and provides a set of utility functions.
The approach is based on thinking about SSGs as providing a couple of key features.

 * A Bundler - in this case I'm using vite
 * Markdown Support - Currently this project uses marked
 * Templating - Currently uses ejs

And that's it.

### Directory Structure

 * templates - store templates
 * static - static assets (images, css, js, etc.)
 * content - input for templates in markdown
 * temp - the directory template output is put in before handled by bundler
 * dist - the output of the bundler and final site

### Important Files
 * naps.ts - the main naps file that contains all of the utility functions used to make a site
 * site.ts - the script that builds the site
