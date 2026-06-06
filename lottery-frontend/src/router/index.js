import {createRouter, createWebHistory} from 'vue-router'
import {adminRouteCatalog, appRouteCatalog, routeCatalog, staticRoutes} from './route-table'
import {ensureSession, hasMenu, hasRole, hasToken, markDynamicRoutes, sessionState} from '../stores/session'

const router = createRouter({
    history: createWebHistory(),
    routes: staticRoutes
})

function installDynamicRoutes() {
    if (sessionState.dynamicRoutesReady) {
        return
    }

    const names = []
    adminRouteCatalog
        .filter((route) => hasRole(route.meta.roles ?? []) && hasMenu(route.meta.menuCode))
        .forEach((route) => {
            router.addRoute('admin', route)
            names.push(route.name)
        })

    appRouteCatalog
        .filter((route) => hasRole(route.meta.roles ?? []) && hasMenu(route.meta.menuCode))
        .forEach((route) => {
            router.addRoute('mobileApp', route)
            names.push(route.name)
        })

    markDynamicRoutes(names)
}

function resolveHomeRoute() {
    const firstAdminRoute = adminRouteCatalog.find((route) => hasRole(route.meta.roles ?? []) && hasMenu(route.meta.menuCode))
    if (firstAdminRoute) {
        return `/admin/${firstAdminRoute.path}`
    }

    const firstAppRoute = appRouteCatalog.find((route) => hasRole(route.meta.roles ?? []) && hasMenu(route.meta.menuCode))
    return firstAppRoute ? `/app/${firstAppRoute.path}` : '/login'
}

function resolveScopedHomeRoute(scope) {
    const catalog = scope === 'admin' ? adminRouteCatalog : appRouteCatalog
    const basePath = scope === 'admin' ? '/admin' : '/app'
    const firstRoute = catalog.find((route) => hasRole(route.meta.roles ?? []) && hasMenu(route.meta.menuCode))

    return firstRoute ? `${basePath}/${firstRoute.path}` : resolveHomeRoute()
}

router.beforeEach(async (to) => {
    if (to.meta.public && !hasToken()) {
        return true
    }

    const profile = await ensureSession()
    const loggedIn = Boolean(profile && hasToken())

    if (!loggedIn && !to.meta.public) {
        return {
            path: '/login',
            query: {
                redirect: to.fullPath
            }
        }
    }

    if (loggedIn && !sessionState.dynamicRoutesReady) {
        installDynamicRoutes()
        if (to.fullPath === '/admin') {
            return resolveScopedHomeRoute('admin')
        }
        if (to.fullPath === '/app') {
            return resolveScopedHomeRoute('app')
        }
        return to.fullPath === '/workspace' ? resolveHomeRoute() : {...to, replace: true}
    }

    if (loggedIn && to.name === 'login') {
        return resolveHomeRoute()
    }

    if ((to.meta.roles && !hasRole(to.meta.roles)) || (to.meta.menuCode && !hasMenu(to.meta.menuCode))) {
        return resolveHomeRoute()
    }

    if (loggedIn && to.fullPath === '/admin') {
        return resolveScopedHomeRoute('admin')
    }

    if (loggedIn && to.fullPath === '/app') {
        return resolveScopedHomeRoute('app')
    }

    if (loggedIn && to.fullPath === '/workspace') {
        return resolveHomeRoute()
    }

    return true
})

export {adminRouteCatalog, appRouteCatalog, routeCatalog, resolveHomeRoute, resolveScopedHomeRoute}
export default router
