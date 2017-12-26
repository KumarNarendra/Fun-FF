import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('Match e2e test', () => {

    let navBarPage: NavBarPage;
    let matchDialogPage: MatchDialogPage;
    let matchComponentsPage: MatchComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Matches', () => {
        navBarPage.goToEntity('match');
        matchComponentsPage = new MatchComponentsPage();
        expect(matchComponentsPage.getTitle()).toMatch(/fafiApp.match.home.title/);

    });

    it('should load create Match dialog', () => {
        matchComponentsPage.clickOnCreateButton();
        matchDialogPage = new MatchDialogPage();
        expect(matchDialogPage.getModalTitle()).toMatch(/fafiApp.match.home.createOrEditLabel/);
        matchDialogPage.close();
    });

    it('should create and save Matches', () => {
        matchComponentsPage.clickOnCreateButton();
        matchDialogPage.setStartDateTimeInput(12310020012301);
        expect(matchDialogPage.getStartDateTimeInput()).toMatch('2001-12-31T02:30');
        matchDialogPage.setEndDateTimeInput(12310020012301);
        expect(matchDialogPage.getEndDateTimeInput()).toMatch('2001-12-31T02:30');
        matchDialogPage.setPointsForFranchise1Input('5');
        expect(matchDialogPage.getPointsForFranchise1Input()).toMatch('5');
        matchDialogPage.setPointsForFranchise2Input('5');
        expect(matchDialogPage.getPointsForFranchise2Input()).toMatch('5');
        matchDialogPage.tournamentSelectLastOption();
        matchDialogPage.franchise1SelectLastOption();
        matchDialogPage.franchise2SelectLastOption();
        matchDialogPage.winnerSelectLastOption();
        matchDialogPage.save();
        expect(matchDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class MatchComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('fafi-match div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class MatchDialogPage {
    modalTitle = element(by.css('h4#myMatchLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    startDateTimeInput = element(by.css('input#field_startDateTime'));
    endDateTimeInput = element(by.css('input#field_endDateTime'));
    pointsForFranchise1Input = element(by.css('input#field_pointsForFranchise1'));
    pointsForFranchise2Input = element(by.css('input#field_pointsForFranchise2'));
    tournamentSelect = element(by.css('select#field_tournament'));
    franchise1Select = element(by.css('select#field_franchise1'));
    franchise2Select = element(by.css('select#field_franchise2'));
    winnerSelect = element(by.css('select#field_winner'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setStartDateTimeInput = function(startDateTime) {
        this.startDateTimeInput.sendKeys(startDateTime);
    }

    getStartDateTimeInput = function() {
        return this.startDateTimeInput.getAttribute('value');
    }

    setEndDateTimeInput = function(endDateTime) {
        this.endDateTimeInput.sendKeys(endDateTime);
    }

    getEndDateTimeInput = function() {
        return this.endDateTimeInput.getAttribute('value');
    }

    setPointsForFranchise1Input = function(pointsForFranchise1) {
        this.pointsForFranchise1Input.sendKeys(pointsForFranchise1);
    }

    getPointsForFranchise1Input = function() {
        return this.pointsForFranchise1Input.getAttribute('value');
    }

    setPointsForFranchise2Input = function(pointsForFranchise2) {
        this.pointsForFranchise2Input.sendKeys(pointsForFranchise2);
    }

    getPointsForFranchise2Input = function() {
        return this.pointsForFranchise2Input.getAttribute('value');
    }

    tournamentSelectLastOption = function() {
        this.tournamentSelect.all(by.tagName('option')).last().click();
    }

    tournamentSelectOption = function(option) {
        this.tournamentSelect.sendKeys(option);
    }

    getTournamentSelect = function() {
        return this.tournamentSelect;
    }

    getTournamentSelectedOption = function() {
        return this.tournamentSelect.element(by.css('option:checked')).getText();
    }

    franchise1SelectLastOption = function() {
        this.franchise1Select.all(by.tagName('option')).last().click();
    }

    franchise1SelectOption = function(option) {
        this.franchise1Select.sendKeys(option);
    }

    getFranchise1Select = function() {
        return this.franchise1Select;
    }

    getFranchise1SelectedOption = function() {
        return this.franchise1Select.element(by.css('option:checked')).getText();
    }

    franchise2SelectLastOption = function() {
        this.franchise2Select.all(by.tagName('option')).last().click();
    }

    franchise2SelectOption = function(option) {
        this.franchise2Select.sendKeys(option);
    }

    getFranchise2Select = function() {
        return this.franchise2Select;
    }

    getFranchise2SelectedOption = function() {
        return this.franchise2Select.element(by.css('option:checked')).getText();
    }

    winnerSelectLastOption = function() {
        this.winnerSelect.all(by.tagName('option')).last().click();
    }

    winnerSelectOption = function(option) {
        this.winnerSelect.sendKeys(option);
    }

    getWinnerSelect = function() {
        return this.winnerSelect;
    }

    getWinnerSelectedOption = function() {
        return this.winnerSelect.element(by.css('option:checked')).getText();
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
