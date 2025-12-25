#!/usr/bin/env node

/**
 * Скрипт для генерации тестового JWT токена
 * Использование: node generate-test-token.js [userId] [secret]
 * 
 * Пример: node generate-test-token.js 1 your_secret_key
 */

const jwt = require('jsonwebtoken');

// Параметры из командной строки или дефолтные значения
const userId = process.argv[2] ? parseInt(process.argv[2]) : 1;
const secret = process.argv[3] || 'test_secret_key_replace_with_real_one';

// Генерируем access token (действует 1 час)
const accessToken = jwt.sign(
  { id: userId }, 
  secret, 
  { expiresIn: 60 * 60 }
);

// Генерируем refresh token (действует 180 дней)
const refreshToken = jwt.sign(
  { id: userId }, 
  secret, 
  { expiresIn: 60 * 60 * 24 * 180 }
);

console.log('\n✅ Токены успешно сгенерированы:\n');
console.log('📝 User ID:', userId);
console.log('🔐 Secret:', secret.substring(0, 10) + '...');
console.log('\n🎫 Access Token (1 час):');
console.log(accessToken);
console.log('\n🎫 Refresh Token (180 дней):');
console.log(refreshToken);
console.log('\n📋 Использование в мобильном приложении:');
console.log('В HttpClientFactory добавьте заголовок:');
console.log('headers.append("Authorization", "Bearer ' + accessToken + '")');
console.log('\n📋 Использование в curl:');
console.log('curl -H "Authorization: Bearer ' + accessToken + '" https://citec.spb.ru/api/project/active');
console.log('\n');



