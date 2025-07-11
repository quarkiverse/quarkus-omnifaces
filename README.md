<div align="center">
  <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
    <img src="https://raw.githubusercontent.com/quarkiverse/.github/main/assets/images/quarkus.svg" alt="Quarkus logo" style="height: 70px; width: auto;">
    <img src="https://raw.githubusercontent.com/quarkiverse/.github/main/assets/images/plus-sign.svg" alt="Plus sign" style="height: 70px; width: auto;">
    <img src="https://github.com/quarkiverse/quarkus-omnifaces/blob/main/docs/modules/ROOT/assets/images/omnifaces.png" alt="OmniFaces logo" style="height: 70px; width: auto;">
  </div>

  <h1>Quarkus OmniFaces</h1>
</div>
<br>


[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.omnifaces/quarkus-omnifaces?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.omnifaces/quarkus-omnifaces)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Quarkus ecosystem CI](https://github.com/quarkiverse/quarkus-omnifaces/actions/workflows/quarkus-snapshot.yaml/badge.svg)](https://github.com/quarkiverse/quarkus-omnifaces/actions/workflows/quarkus-snapshot.yaml)
[![Stackoverflow](https://img.shields.io/badge/StackOverflow-omnifaces-chocolate.svg)](https://stackoverflow.com/questions/tagged/omnifaces)

## Overview

A Quarkus extension that lets you utilize [OmniFaces](https://omnifaces.org/) library to make JSF development so much easier!

This extension was previously written by the AdminFaces team and hosted [here](https://github.com/adminfaces/quarkus-omnifaces).

## Getting started

Read the full [OmniFaces documentation](https://docs.quarkiverse.io/quarkus-omnifaces/dev/index.html).

### Prerequisite

* Create or use an existing Quarkus application
* Add the OmniFaces extension with the [Quarkus CLI](https://quarkus.io/guides/cli-tooling):
```bash
quarkus ext add io.quarkiverse.omnifaces:quarkus-omnifaces
```

## Versioning

The versioning of this extension will follow the pattern `[faces.version].[omnifaces.version].[patch]`. For example:

| Version | Explanation |
| --- | --- |
| ![3.x](https://img.shields.io/maven-central/v/io.quarkiverse.omnifaces/quarkus-omnifaces?versionPrefix=3.&color=cyan) | Quarkus 2 (EE8), OmniFaces 3 |
| ![4.x](https://img.shields.io/maven-central/v/io.quarkiverse.omnifaces/quarkus-omnifaces?versionPrefix=4.&color=cyan) | Quarkus 3 (EE10), OmniFaces 4 |
| ![5.x](https://img.shields.io/maven-central/v/io.quarkiverse.omnifaces/quarkus-omnifaces?versionPrefix=5.&color=cyan) | Quarkus 3 (EE11), OmniFaces 5 |

> [!IMPORTANT]  
> `@Param` component is not supported in GraalVM Native Image mode due to complexities with how it uses Reflection to inject fields.

## Showcase

A showcase example using Quarkus OmniFaces and showing Faces running in the Quarkus environment can be found on
the [QuarkusFaces](https://github.com/melloware/quarkus-faces) GitHub repository "where Quarkus meets JSF!".

[![Quarkus Faces Logo](https://github.com/melloware/quarkus-faces/blob/main/src/site/QuarkusFaces.svg)](https://github.com/melloware/quarkus-faces)

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="http://melloware.com"><img src="https://avatars.githubusercontent.com/u/4399574?v=4?s=100" width="100px;" alt="Melloware"/><br /><sub><b>Melloware</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/commits?author=melloware" title="Code">💻</a> <a href="#maintenance-melloware" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://rpestano.wordpress.com"><img src="https://avatars.githubusercontent.com/u/1592273?v=4?s=100" width="100px;" alt="Rafael Pestano"/><br /><sub><b>Rafael Pestano</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/commits?author=rmpestano" title="Code">💻</a> <a href="#maintenance-rmpestano" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://balusc.omnifaces.org"><img src="https://avatars.githubusercontent.com/u/173372?v=4?s=100" width="100px;" alt="Bauke Scholtz"/><br /><sub><b>Bauke Scholtz</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/commits?author=BalusC" title="Code">💻</a> <a href="#maintenance-BalusC" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://arjan-tijms.omnifaces.org"><img src="https://avatars.githubusercontent.com/u/3037006?v=4?s=100" width="100px;" alt="Arjan Tijms"/><br /><sub><b>Arjan Tijms</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/commits?author=arjantijms" title="Code">💻</a> <a href="#maintenance-arjantijms" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://www.phillip-kruger.com"><img src="https://avatars.githubusercontent.com/u/6836179?v=4?s=100" width="100px;" alt="Phillip Krüger"/><br /><sub><b>Phillip Krüger</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/issues?q=author%3Aphillip-kruger" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://gastaldi.wordpress.com"><img src="https://avatars.githubusercontent.com/u/54133?v=4?s=100" width="100px;" alt="George Gastaldi"/><br /><sub><b>George Gastaldi</b></sub></a><br /><a href="#infra-gastaldi" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/felipelx07"><img src="https://avatars.githubusercontent.com/u/5168904?v=4?s=100" width="100px;" alt="Felipe Miranda"/><br /><sub><b>Felipe Miranda</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/issues?q=author%3Afelipelx07" title="Bug reports">🐛</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="http://www.activi.link"><img src="https://avatars.githubusercontent.com/u/56843747?v=4?s=100" width="100px;" alt="zakhdar"/><br /><sub><b>zakhdar</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/issues?q=author%3Azakhdar" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/manovotn"><img src="https://avatars.githubusercontent.com/u/4181235?v=4?s=100" width="100px;" alt="Matej Novotny"/><br /><sub><b>Matej Novotny</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/commits?author=manovotn" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://www.sven-haag.de"><img src="https://avatars.githubusercontent.com/u/1632624?v=4?s=100" width="100px;" alt="svenhaag"/><br /><sub><b>svenhaag</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/commits?author=svenhaag" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/dstutz"><img src="https://avatars.githubusercontent.com/u/6010885?v=4?s=100" width="100px;" alt="David Stutzman"/><br /><sub><b>David Stutzman</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-omnifaces/issues?q=author%3Adstutz" title="Bug reports">🐛</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
