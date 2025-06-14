/**
 * Markdown Editor для CodeWiki
 * Использует:
 * - EasyMDE для редактора
 * - Highlight.js для подсветки кода
 * - Auto-save в localStorage
 */

class CodeWikiEditor {
  constructor(options = {}) {
    // Конфигурация по умолчанию
    this.defaultOptions = {
      elementId: 'editor',
      previewId: 'preview',
      autoSaveKey: 'codewiki_draft',
      autoSaveInterval: 5000,
      uploadUrl: '/api/upload'
    };

    this.config = { ...this.defaultOptions, ...options };
    this.editor = null;
    this.isDraftSaved = false;
    this.init();
  }

  // Инициализация редактора
  init() {
    this.setupEditor();
    this.setupEventListeners();
    this.loadDraft();
    this.setupAutoSave();
  }

  // Настройка EasyMDE
  setupEditor() {
    this.editor = new EasyMDE({
      element: document.getElementById(this.config.elementId),
      autoDownloadFontAwesome: false,
      spellChecker: false,
      placeholder: 'Начните писать статью в Markdown...',
      previewClass: 'markdown-content',
      toolbar: [
        'bold', 'italic', 'heading', '|',
        'quote', 'unordered-list', 'ordered-list', '|',
        'link', 'image', '|',
        'preview', 'side-by-side', 'fullscreen', '|',
        {
          name: 'customSave',
          action: this.saveArticle.bind(this),
          className: 'fa fa-save',
          title: 'Сохранить статью'
        }
      ],
      uploadImage: true,
      imageUploadFunction: this.uploadImage.bind(this),
      renderingConfig: {
        codeSyntaxHighlighting: true
      }
    });

    // Инициализация Highlight.js
    if (window.hljs) {
      this.editor.codemirror.on('update', () => {
        document.querySelectorAll('.markdown-content pre code').forEach((block) => {
          hljs.highlightElement(block);
        });
      });
    }
  }

  // Загрузка черновика из localStorage
  loadDraft() {
    const draft = localStorage.getItem(this.config.autoSaveKey);
    if (draft) {
      this.editor.value(draft);
      this.showNotification('Черновик восстановлен', 'info');
    }
  }

  // Автосохранение
  setupAutoSave() {
    this.autoSaveInterval = setInterval(() => {
      const content = this.editor.value();
      if (content.trim().length > 0) {
        localStorage.setItem(this.config.autoSaveKey, content);
        if (!this.isDraftSaved) {
          this.showNotification('Черновик сохранён', 'success');
          this.isDraftSaved = true;
        }
      }
    }, this.config.autoSaveInterval);
  }

  // Загрузка изображений
  async uploadImage(file, onSuccess, onError) {
    const formData = new FormData();
    formData.append('image', file);

    try {
      const response = await fetch(this.config.uploadUrl, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('codewiki_jwt')}`
        },
        body: formData
      });

      if (!response.ok) throw new Error('Upload failed');

      const data = await response.json();
      onSuccess(data.url);
    } catch (error) {
      console.error('Upload error:', error);
      onError(error.message);
      this.showNotification(`Ошибка загрузки: ${error.message}`, 'error');
    }
  }

  // Сохранение статьи
  async saveArticle() {
    const content = this.editor.value();
    if (content.trim().length === 0) {
      this.showNotification('Статья не может быть пустой', 'error');
      return;
    }

    try {
      const response = await fetch('/api/articles', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('codewiki_jwt')}`
        },
        body: JSON.stringify({
          title: this.extractTitle(content),
          content: content,
          status: 'draft'
        })
      });

      if (!response.ok) throw new Error('Save failed');

      const data = await response.json();
      localStorage.removeItem(this.config.autoSaveKey);
      this.showNotification('Статья сохранена!', 'success');
      setTimeout(() => {
        window.location.href = `/article.html?id=${data.id}`;
      }, 1500);
    } catch (error) {
      console.error('Save error:', error);
      this.showNotification(`Ошибка: ${error.message}`, 'error');
    }
  }

  // Извлечение заголовка из Markdown
  extractTitle(content) {
    const match = content.match(/^#\s(.+)$/m);
    return match ? match[1] : 'Без названия';
  }

  // Уведомления
  showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `editor-notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
      notification.classList.add('fade-out');
      setTimeout(() => notification.remove(), 500);
    }, 3000);
  }

  // Очистка
  destroy() {
    clearInterval(this.autoSaveInterval);
    this.editor.cleanup();
  }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
  window.editor = new CodeWikiEditor();
});

// CSS для редактора (добавьте в style.css)
const editorStyles = `
  .editor-notification {
    position: fixed;
    bottom: 20px;
    right: 20px;
    padding: 12px 24px;
    border-radius: 4px;
    color: white;
    z-index: 1000;
    transition: opacity 0.5s;
  }
  .editor-notification.info { background: #2196F3; }
  .editor-notification.success { background: #4CAF50; }
  .editor-notification.error { background: #F44336; }
  .editor-notification.fade-out { opacity: 0; }
  .EasyMDE { border: 1px solid #ddd; border-radius: 4px; }
  .markdown-content img { max-width: 100%; }
`;

const styleElement = document.createElement('style');
styleElement.textContent = editorStyles;
document.head.appendChild(styleElement);