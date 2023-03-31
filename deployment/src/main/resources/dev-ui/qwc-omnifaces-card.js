import { LitElement, html, css} from 'lit';
import { pages } from 'build-time-data';
import {JsonRpc} from 'jsonrpc';
import 'qwc/qwc-extension-link.js';

// deprecated. From CR2 onwards this will be passed in at a property (extensionName)
const NAME = "OmniFaces";
export class QwcOmniFacesCard extends LitElement {

    jsonRpc = new JsonRpc(this);

    static styles = css`
      .identity {
        display: flex;
        justify-content: flex-start;
      }

      .description {
        padding-bottom: 10px;
      }

      .logo {
        padding-bottom: 10px;
        margin-right: 5px;
      }

      .card-content {
        color: var(--lumo-contrast-90pct);
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        padding: 2px 2px;
        height: 100%;
      }

      .card-content slot {
        display: flex;
        flex-flow: column wrap;
        padding-top: 5px;
      }
    `;

    static properties = {
        extensionName: {attribute: true},
        description: {attribute: true},
        guide: {attribute: true},
        namespace: {attribute: true},
    };

    constructor() {
        super();
        if(!this.extensionName){
            this.extensionName = NAME;
        }
    }

    connectedCallback() {
        super.connectedCallback();
    }

    render() {
        return html`<div class="card-content" slot="content">
            <div class="identity">
                <div class="logo">
                    <img src="data:image/png;base64,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"
                                       alt="${this.extensionName}" 
                                       title="${this.extensionName}"
                                       width="32" 
                                       height="32">
                </div>
                <div class="description">${this.description}</div>
            </div>
            ${this._renderCardLinks()}
        </div>
        `;
    }

    _renderCardLinks(){
        return html`${pages.map(page => html`
                            <qwc-extension-link slot="link"
                                namespace="${this.namespace}"
                                extensionName="${this.extensionName}"
                                iconName="${page.icon}"
                                displayName="${page.title}"
                                staticLabel="${page.staticLabel}"
                                dynamicLabel="${page.dynamicLabel}"
                                streamingLabel="${page.streamingLabel}"
                                path="${page.id}"
                                ?embed=${page.embed}
                                externalUrl="${page.metadata.externalUrl}"
                                webcomponent="${page.componentLink}" >
                            </qwc-extension-link>
                        `)}`;
    }

}
customElements.define('qwc-omnifaces-card', QwcOmniFacesCard);