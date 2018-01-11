import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';
import * as path from 'path';
describe('Player e2e test', () => {

    let navBarPage: NavBarPage;
    let playerDialogPage: PlayerDialogPage;
    let playerComponentsPage: PlayerComponentsPage;
    const fileToUpload = '../../../../main/webapp/content/images/logo-jhipster.png';
    const absolutePath = path.resolve(__dirname, fileToUpload);

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Players', () => {
        navBarPage.goToEntity('player');
        playerComponentsPage = new PlayerComponentsPage();
        expect(playerComponentsPage.getTitle()).toMatch(/fafiApp.player.home.title/);

    });

    it('should load create Player dialog', () => {
        playerComponentsPage.clickOnCreateButton();
        playerDialogPage = new PlayerDialogPage();
        expect(playerDialogPage.getModalTitle()).toMatch(/fafiApp.player.home.createOrEditLabel/);
        playerDialogPage.close();
    });

    it('should create and save Players', () => {
        playerComponentsPage.clickOnCreateButton();
        playerDialogPage.setNameInput('name');
        expect(playerDialogPage.getNameInput()).toMatch('name');
        playerDialogPage.setBasePriceInput('5');
        expect(playerDialogPage.getBasePriceInput()).toMatch('5');
        playerDialogPage.setBidPriceInput('5');
        expect(playerDialogPage.getBidPriceInput()).toMatch('5');
        playerDialogPage.optedGamesSelectLastOption();
        playerDialogPage.setPointsInput('5');
        expect(playerDialogPage.getPointsInput()).toMatch('5');
        playerDialogPage.setProfilePicInput(absolutePath);
        playerDialogPage.franchiseSelectLastOption();
        playerDialogPage.save();
        expect(playerDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class PlayerComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('fafi-player div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class PlayerDialogPage {
    modalTitle = element(by.css('h4#myPlayerLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));
    basePriceInput = element(by.css('input#field_basePrice'));
    bidPriceInput = element(by.css('input#field_bidPrice'));
    optedGamesSelect = element(by.css('select#field_optedGames'));
    pointsInput = element(by.css('input#field_points'));
    profilePicInput = element(by.css('input#file_profilePic'));
    franchiseSelect = element(by.css('select#field_franchise'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setNameInput = function(name) {
        this.nameInput.sendKeys(name);
    }

    getNameInput = function() {
        return this.nameInput.getAttribute('value');
    }

    setBasePriceInput = function(basePrice) {
        this.basePriceInput.sendKeys(basePrice);
    }

    getBasePriceInput = function() {
        return this.basePriceInput.getAttribute('value');
    }

    setBidPriceInput = function(bidPrice) {
        this.bidPriceInput.sendKeys(bidPrice);
    }

    getBidPriceInput = function() {
        return this.bidPriceInput.getAttribute('value');
    }

    setOptedGamesSelect = function(optedGames) {
        this.optedGamesSelect.sendKeys(optedGames);
    }

    getOptedGamesSelect = function() {
        return this.optedGamesSelect.element(by.css('option:checked')).getText();
    }

    optedGamesSelectLastOption = function() {
        this.optedGamesSelect.all(by.tagName('option')).last().click();
    }
    setPointsInput = function(points) {
        this.pointsInput.sendKeys(points);
    }

    getPointsInput = function() {
        return this.pointsInput.getAttribute('value');
    }

    setProfilePicInput = function(profilePic) {
        this.profilePicInput.sendKeys(profilePic);
    }

    getProfilePicInput = function() {
        return this.profilePicInput.getAttribute('value');
    }

    franchiseSelectLastOption = function() {
        this.franchiseSelect.all(by.tagName('option')).last().click();
    }

    franchiseSelectOption = function(option) {
        this.franchiseSelect.sendKeys(option);
    }

    getFranchiseSelect = function() {
        return this.franchiseSelect;
    }

    getFranchiseSelectedOption = function() {
        return this.franchiseSelect.element(by.css('option:checked')).getText();
    }

    save() {
        this.saveButton.click();
    }

    close() {
        this.closeButton.click();
    }

    getSaveButton() {
        return this.saveButton;
    }
}
