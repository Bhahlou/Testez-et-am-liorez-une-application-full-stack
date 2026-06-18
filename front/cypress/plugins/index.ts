/**
 * @type {Cypress.PluginConfig}
 */
import registerCodeCoverageTasks from '@cypress/code-coverage/task';

const index = (
  on: Cypress.PluginEvents,
  config: Cypress.PluginConfigOptions,
) => {
  return registerCodeCoverageTasks(on, config);
};
export default index;
