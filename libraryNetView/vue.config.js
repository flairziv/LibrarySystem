const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
    lintOnSave: false,
    devServer: {
        host: "localhost",
        port: 8080,
        https: false,
        proxy: "http://localhost:2025",
        client: {
            overlay: {
                warnings: false,
                errors: false
            }
        }
    },
    configureWebpack: {
        plugins: [
            new (require('webpack')).DefinePlugin({
                __VUE_OPTIONS_API__: JSON.stringify(true),
                __VUE_PROD_DEVTOOLS__: JSON.stringify(false),
                __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false)
            })
        ]
    },
    css: {
        loaderOptions: {
            sass: {
                api: 'modern',
                sassOptions: {
                    silenceDeprecations: ['legacy-js-api']
                }
            },
            scss: {
                api: 'modern',
                sassOptions: {
                    silenceDeprecations: ['legacy-js-api']
                }
            }
        }
    }
})
