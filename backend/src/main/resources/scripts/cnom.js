const deepDiff = require('deep-diff');
const fs = require('fs');
const path = require('path');
const { fetch } = require('undici');

// Configuration
const DEFAULT_DATA_FOLDER = path.resolve(__dirname, 'data');
const DEFAULT_CHANGES_FOLDER = path.resolve(__dirname, 'changes');

class DataManager {

  constructor(apiUrl, dataFolder = DEFAULT_DATA_FOLDER, changesFolder = DEFAULT_CHANGES_FOLDER) {
    this.apiUrl = apiUrl;
    this.dataFolder = dataFolder;
    this.changesFolder = changesFolder;
    this.ensureDirectories();
  }

  formatDate(date = new Date()) {
    return date.toISOString().split('T')[0];
  }

  ensureDirectories() {
    [this.dataFolder, this.changesFolder].forEach(dir => {
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }
    });
  }

  formatDate(date = new Date()) {
    return date.toISOString().split('T')[0];
  }

  async fetchData() {
    try {
      const response = await fetch(this.apiUrl);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      
      const data = await response.json();
      const todayFile = path.join(this.dataFolder, `${this.formatDate()}.json`);
      fs.writeFileSync(todayFile, JSON.stringify(data, null, 2));
      return true;

    } catch (error) {
      console.error(`Fetch error: ${error.message}`);
      return false;
    }
  }

  compareDatasets() {
    const today = this.formatDate();
    const yesterday = this.formatDate(new Date(Date.now() - 86400000));
    
    const currentPath = path.join(this.dataFolder, `${today}.json`);
    const previousPath = path.join(this.dataFolder, `${yesterday}.json`);

    if (!fs.existsSync(currentPath)) return null;

    try {
      const currentData = JSON.parse(fs.readFileSync(currentPath, 'utf8'));
      const previousData = fs.existsSync(previousPath) 
        ? JSON.parse(fs.readFileSync(previousPath, 'utf8'))
        : [];

      const changes = {
        inserts: [],
        updates: [],
        deletes: [],
        summary: {
          total_added: 0,
          total_updated: 0,
          total_removed: 0
        }
      };
      const previousMap = new Map(previousData.map(item => [item.NumeroOrdre, item]));
      const currentMap = new Map(currentData.map(item => [item.NumeroOrdre, item]));
      currentData.forEach(item => {
        if (!previousMap.has(item.NumeroOrdre)) {
          changes.inserts.push(item);
          changes.summary.total_added++;
        }
      });
      previousData.forEach(item => {
        if (!currentMap.has(item.NumeroOrdre)) {
          changes.deletes.push(item);
          changes.summary.total_removed++;
        }
      });
      previousData.forEach(oldItem => {
        const newItem = currentMap.get(oldItem.NumeroOrdre);
        if (newItem) {
          const diff = deepDiff.diff(oldItem, newItem);
          if (diff && diff.length > 0) {
            changes.updates.push({
              NumeroOrdre: oldItem.NumeroOrdre,
              changes: diff.map(change => ({
                field: change.path.join('.'),
                old_value: change.lhs,
                new_value: change.rhs
              }))
            });
            changes.summary.total_updated++;
          }
        }
      });
      return changes;
    } catch (error) {
      console.error(`Comparison error: ${error.message}`);
      return null;
    }
  }

  async executeDailyWorkflow() {
     const dateStamp = this.formatDate();

    const changes = this.compareDatasets();
    if (!changes) return;
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const dbOps = {
      inserts: changes.inserts,
      updates: changes.updates,
      deletes: changes.deletes
    };
    
    fs.writeFileSync(
      path.join(this.changesFolder, `db-changes-${dateStamp}.json`),
      JSON.stringify(dbOps, null, 2)
    );
    const summary = [
      `Data Changes Report - ${new Date().toLocaleDateString()}`,
      `=============================================`,
      `New Entries:    ${changes.summary.total_added}`,
      `Updated Entries: ${changes.summary.total_updated}`,
      `Removed Entries: ${changes.summary.total_removed}`,
      ``,
      `Sample Changes:`,
      ...changes.inserts.map(i => `[+] ${i.NumeroOrdre}: ${i.Nom} ${i.Prenom}`),
      ...changes.updates.map(u => `[≈] ${u.NumeroOrdre}: ${u.changes.length} fields updated`),
      ...changes.deletes.map(d => `[-] ${d.NumeroOrdre}: ${d.Nom} ${d.Prenom}`)
    ].join('\n');

    fs.writeFileSync(
      path.join(this.changesFolder, `changes-summary-${dateStamp}.txt`),
      summary
    );
  }
}

// CLI Execution
(async () => {
  if (process.argv.length < 3) {
    console.log('Usage: node script.js <api-url> [data-dir] [changes-dir]');
    process.exit(1);
  }

  const apiUrl = process.argv[2];
  const dataDir = process.argv[3] || DEFAULT_DATA_FOLDER;
  const changesDir = process.argv[4] || DEFAULT_CHANGES_FOLDER;

  const manager = new DataManager(apiUrl, dataDir, changesDir);
  await manager.executeDailyWorkflow();
})();